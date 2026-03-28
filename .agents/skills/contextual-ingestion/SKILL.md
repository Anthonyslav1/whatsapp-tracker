---
name: contextual-ingestion
description: >
  Use this skill when building or modifying the core tracking engine that reads
  the Android Accessibility node tree to detect what the user is viewing inside
  WhatsApp. Triggers for any work involving TrackerService, AccessibilityService,
  WhatsAppAccessibilityParser, BFS node traversal, contact-name extraction,
  session-type detection (Chat vs Status), noise filtering, or the construction
  of the raw SessionDTO that feeds the persistence layer. If the user mentions
  "parsing WhatsApp UI", "detecting contact name", "accessibility events",
  "node tree traversal", or "filtering UI noise", always use this skill.
---

# Contextual Ingestion Skill — The Eyes of Ravdesk

## 1. Overview

**Mission:** Silently observe the Android Accessibility node tree while WhatsApp
is in the foreground. Identify the active contact/group name, distinguish between
Chat and Status sessions, and emit a clean `RawSessionEvent` for the persistence
layer — without ever reading message content.

**Core principle:** Filter aggressively. The Accessibility tree is full of noise.
The parser's primary job is *rejection*, not collection.

---

## 2. When to Use

- Building or modifying `TrackerService.kt` (the `AccessibilityService` subclass)
- Building or modifying `WhatsAppAccessibilityParser.kt`
- Debugging sessions being logged with wrong contact names, or noise strings
  ("Search", "Settings", etc.) appearing in the database
- Implementing debounce logic to prevent duplicate session events on rapid screen changes
- Adding new WhatsApp UI surface detection (e.g., Channels, Communities)

## 3. When NOT to Use

- Calculating engagement scores or relationship rankings → use `engagement-scoring` skill
- Saving data to Room / WorkManager enqueueing → use `resilient-persistence` skill
- Compiling or formatting Wrapped/Meta reports → use `viral-payload-generator` skill

---

## 4. Architecture: How the Parser Works

```
Android OS
    │  onAccessibilityEvent(event)
    ▼
TrackerService.kt                  ← AccessibilityService subclass
    │  getRootInActiveWindow()
    │  debounce (300ms)
    ▼
WhatsAppAccessibilityParser.kt     ← Pure logic class (no Android deps at runtime)
    │  traverseNodeTree(rootNode)   ← Recursive BFS
    │  classifyScreen()             ← Chat | Status | Unknown
    │  extractContactName()         ← Strip noise, return clean name
    ▼
RawSessionEvent(
  contactName: String,
  sessionType: SessionType,         ← CHAT | STATUS
  startTimestamp: Long
)
    │
    ▼
SessionManager.kt                  ← Manages open/close of a session
```

---

## 5. Key Implementation Patterns

### 5.1 AccessibilityService Registration (`TrackerService.kt`)

```kotlin
@AndroidEntryPoint
class TrackerService : AccessibilityService() {

    @Inject lateinit var parser: WhatsAppAccessibilityParser
    @Inject lateinit var sessionManager: SessionManager

    // Debounce: ignore rapid re-fires (e.g. keyboard open/close)
    private var lastProcessedTimestamp = 0L
    private val DEBOUNCE_MS = 300L

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val now = System.currentTimeMillis()
        if (now - lastProcessedTimestamp < DEBOUNCE_MS) return
        lastProcessedTimestamp = now

        if (event.packageName != WHATSAPP_PACKAGE) return

        val root = rootInActiveWindow ?: return
        val result = parser.parse(root)
        root.recycle() // ALWAYS recycle to prevent memory leaks

        when (result) {
            is ParseResult.Active -> sessionManager.onScreenActive(result.event)
            is ParseResult.Inactive -> sessionManager.onScreenInactive()
        }
    }

    override fun onInterrupt() = sessionManager.onScreenInactive()

    companion object {
        const val WHATSAPP_PACKAGE = "com.whatsapp"
    }
}
```

### 5.2 BFS Node Traversal (`WhatsAppAccessibilityParser.kt`)

```kotlin
class WhatsAppAccessibilityParser @Inject constructor() {

    fun parse(root: AccessibilityNodeInfo): ParseResult {
        val allText = mutableListOf<String>()
        traverseBFS(root, allText)

        val sessionType = classifyScreen(allText)
        if (sessionType == SessionType.UNKNOWN) return ParseResult.Inactive

        val contactName = extractContactName(allText, sessionType)
            ?: return ParseResult.Inactive

        return ParseResult.Active(
            RawSessionEvent(contactName, sessionType, System.currentTimeMillis())
        )
    }

    // BFS: breadth-first to match WhatsApp's shallow header structure
    private fun traverseBFS(root: AccessibilityNodeInfo, output: MutableList<String>) {
        val queue: ArrayDeque<AccessibilityNodeInfo> = ArrayDeque()
        queue.add(root)

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            val text = node.text?.toString()?.trim()
            if (!text.isNullOrBlank() && !NOISE_STRINGS.contains(text.lowercase())) {
                output.add(text)
            }
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }
    }
}
```

### 5.3 Noise Filter — The Most Important List

```kotlin
// NOISE_STRINGS: UI chrome that must NEVER be logged as a contact name.
// Add to this set whenever a new false positive is discovered in QA.
private val NOISE_STRINGS = setOf(
    "search", "settings", "new chat", "whatsapp", "chats",
    "calls", "status", "communities", "camera", "mute",
    "archive", "pinned", "starred", "notifications", "privacy",
    "storage", "help", "back", "more options", "ok", "cancel",
    "done", "send", "attach", "emoji", "voice message",
    "type a message", "reply", "forward", "delete", "copy",
    // Status-specific noise
    "add to status", "view once", "mute", "report", "view status",
)
```

### 5.4 Screen Classification

```kotlin
private fun classifyScreen(texts: List<String>): SessionType {
    // Status screen always contains these WhatsApp-specific markers
    val statusMarkers = setOf("my status", "recent updates", "viewed by")
    if (texts.any { it.lowercase() in statusMarkers }) return SessionType.STATUS

    // Chat screen: toolbar has contact name + typically "online"/"last seen"
    val chatMarkers = setOf("online", "last seen", "typing...", "recording...")
    if (texts.any { it.lowercase() in chatMarkers }) return SessionType.CHAT

    // Heuristic: if top-level text doesn't match any known list screen,
    // treat a single short string (< 50 chars) as a contact name in a chat
    val candidate = texts.firstOrNull { it.length in 2..50 }
    if (candidate != null && !KNOWN_LIST_SCREENS.contains(texts.firstOrNull()?.lowercase())) {
        return SessionType.CHAT
    }

    return SessionType.UNKNOWN
}

private val KNOWN_LIST_SCREENS = setOf("chats", "calls", "status", "communities")
```

### 5.5 Contact Name Extraction

```kotlin
private fun extractContactName(texts: List<String>, type: SessionType): String? {
    return when (type) {
        SessionType.CHAT -> {
            // The contact name is the FIRST non-noise string in a chat screen
            texts.firstOrNull { candidate ->
                candidate.length in 2..80 &&
                !NOISE_STRINGS.contains(candidate.lowercase()) &&
                !looksLikeTimestamp(candidate) &&
                !looksLikeMessagePreview(candidate)
            }
        }
        SessionType.STATUS -> {
            // For statuses, find the name above the status ring
            // It appears early in the BFS traversal — take the first short string
            texts.filter { it.length in 2..50 && !NOISE_STRINGS.contains(it.lowercase()) }
                .firstOrNull()
        }
        else -> null
    }
}

private fun looksLikeTimestamp(text: String) =
    text.matches(Regex("""\d{1,2}:\d{2}(?: [AP]M)?""")) ||
    text.matches(Regex("""(?:Yesterday|Mon|Tue|Wed|Thu|Fri|Sat|Sun)"""))

private fun looksLikeMessagePreview(text: String) =
    text.startsWith("📷") || text.startsWith("🎥") ||
    text.startsWith("You: ") || text.length > 80
```

---

## 6. Data Contracts

### `RawSessionEvent`
```kotlin
data class RawSessionEvent(
    val contactName: String,      // Cleaned, noise-free display name
    val sessionType: SessionType, // CHAT | STATUS
    val startTimestamp: Long      // System.currentTimeMillis() at detection
)

enum class SessionType { CHAT, STATUS, UNKNOWN }
```

### `ParseResult`
```kotlin
sealed class ParseResult {
    data class Active(val event: RawSessionEvent) : ParseResult()
    object Inactive : ParseResult()
}
```

---

## 7. Accessibility Service Manifest Config

```xml
<!-- AndroidManifest.xml -->
<service
    android:name=".service.TrackerService"
    android:exported="false"
    android:label="@string/app_name"
    android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
    <intent-filter>
        <action android:name="android.accessibilityservice.AccessibilityService" />
    </intent-filter>
    <meta-data
        android:name="android.accessibilityservice"
        android:resource="@xml/accessibility_service_config" />
</service>
```

```xml
<!-- res/xml/accessibility_service_config.xml -->
<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
    android:accessibilityEventTypes="typeWindowStateChanged|typeWindowContentChanged"
    android:accessibilityFeedbackType="feedbackGeneric"
    android:accessibilityFlags="flagDefault|flagIncludeNotImportantViews"
    android:canRetrieveWindowContent="true"
    android:notificationTimeout="100"
    android:packageNames="com.whatsapp" />
```

---

## 8. Testing Strategy

The parser is pure logic — no Activity, no real Accessibility tree needed.

```kotlin
// Test with MockK to build fake AccessibilityNodeInfo trees
@Test
fun `extracts contact name from chat screen`() {
    val root = buildMockNodeTree(listOf("Alice", "online", "Hey how are you"))
    val result = parser.parse(root)
    assertThat(result).isInstanceOf(ParseResult.Active::class.java)
    assertThat((result as ParseResult.Active).event.contactName).isEqualTo("Alice")
    assertThat(result.event.sessionType).isEqualTo(SessionType.CHAT)
}

@Test
fun `ignores noise-only screen (chats list)`() {
    val root = buildMockNodeTree(listOf("Chats", "Search", "New Chat"))
    val result = parser.parse(root)
    assertThat(result).isInstanceOf(ParseResult.Inactive::class.java)
}
```

---

## 9. Error Handling & Gotchas

- **Always recycle `AccessibilityNodeInfo` objects.** Failing to call `root.recycle()` causes a VM memory leak that accumulates across thousands of events.
- **WhatsApp updates silently change their UI tree.** Pin a known working WhatsApp APK version in your test matrix. After any WhatsApp update, immediately run the full parser test suite.
- **Emulators are unreliable for this.** The real WhatsApp APK does not run correctly on emulators. All QA of the parser must happen on a physical device.
- **Don't log `AccessibilityEvent.TYPE_VIEW_CLICKED`** — it fires for every tap and produces enormous noise. Stick to `typeWindowStateChanged` + `typeWindowContentChanged`.
- **Group chats**: group names can be long (up to 100 chars). Adjust the length filter ceiling if group tracking is a goal.
