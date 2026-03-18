package com.whatsapptracker.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.Job
import javax.inject.Inject

@AndroidEntryPoint
class WhatsAppAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var parser: WhatsAppAccessibilityParser

    @Inject
    lateinit var sessionTracker: SessionTrackerManager

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var debounceJob: Job? = null

    private val whatsappPackages = setOf(
        "com.whatsapp",
        "com.whatsapp.w4b",
        "com.whatsapp.w4b.beta",
        "com.whatsapp.beta",
        "com.whatsapp.business"
    )

    companion object {
        private const val TAG = "WhatsAppAccessibility"
        private var debugMode = false

        // Known WhatsApp home / list screens — ending a session here is safe
        private val HOME_SCREEN_PATTERNS = listOf(
            "TabbedActivity",
            "HomeActivity",
            "MainActivity",
            "HomeScreen",
            "ConversationListActivity"
        )

        // Known non-chat screens where we should always end the session
        private val EXIT_SCREEN_PATTERNS = listOf(
            "Settings",
            "Profile",
            "About",
            "MediaViewer",
            "MediaGallery",
            "BlockedContacts",
            "PrivacySettings",
            "NotificationSettings",
            "StorageUsage",
            "NetworkUsage",
            "Help",
            "InviteFriend"
        )

        // Status playback screen patterns
        private val STATUS_SCREEN_PATTERNS = listOf(
            "StatusPlaybackActivity",
            "StatusViewer",
            "StatusView",
            "StatusPlayer",
            "StatusPager"
        )

        // Chat conversation screen patterns
        private val CHAT_SCREEN_PATTERNS = listOf(
            "Conversation",
            "ConversationActivity",
            "ChatActivity",
            "MessageListActivity"
        )
    }

    override fun onServiceConnected() {
        Log.d(TAG, "Accessibility service connected")
        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }
        Log.d(TAG, "Service configured")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        val className = event.className?.toString() ?: return

        if (debugMode) Log.d(TAG, "Window changed: package=$packageName class=$className")

        // Ignore system UI / inner class names
        if (className.contains("$") || packageName == "android") return

        if (whatsappPackages.contains(packageName)) {
            Log.d(TAG, "WhatsApp event: $className")
            handleWhatsAppScreen(className)
        } else if (sessionTracker.currentChatName != null) {
            Log.d(TAG, "Left WhatsApp (now in $packageName) — ending session")
            sessionTracker.endSession()
        }
    }

    private fun handleWhatsAppScreen(className: String) {
        debounceJob?.cancel()
        debounceJob = serviceScope.launch {
            delay(300)
            Log.d(TAG, "Processing screen: $className")

            when {
                // ── Chat conversation ────────────────────────────────────────
                CHAT_SCREEN_PATTERNS.any { className.contains(it, ignoreCase = true) } -> {
                    Log.d(TAG, "Chat screen detected")
                    handleChatScreen()
                }

                // ── Status viewer ────────────────────────────────────────────
                STATUS_SCREEN_PATTERNS.any { className.contains(it, ignoreCase = true) } -> {
                    Log.d(TAG, "Status screen detected")
                    handleStatusScreen()
                }

                // ── Known home / list screens — end session ──────────────────
                HOME_SCREEN_PATTERNS.any { className.contains(it, ignoreCase = true) } -> {
                    Log.d(TAG, "Home/list screen detected — ending session")
                    sessionTracker.endSession()
                }

                // ── Known non-chat screens — end session ─────────────────────
                EXIT_SCREEN_PATTERNS.any { className.contains(it, ignoreCase = true) } -> {
                    Log.d(TAG, "Exit/settings screen detected ($className) — ending session")
                    sessionTracker.endSession()
                }

                // ── Unknown WhatsApp screen ──────────────────────────────────
                // Do NOT blindly end the session here.  Many internal WhatsApp
                // dialogs, permission prompts, and overlays fire
                // TYPE_WINDOW_STATE_CHANGED with unrecognised class names while
                // the user is still actively reading a chat.  Ending the session
                // would fragment it into sub-minute slices that look like <1m.
                //
                // Strategy:
                //   • If a session is already active → leave it running.
                //   • If no session is active → attempt a fallback detection in
                //     case this unknown screen actually IS a chat.
                else -> {
                    Log.d(TAG, "Unknown WhatsApp screen: $className")
                    if (sessionTracker.currentChatName != null) {
                        // Keep the active session alive through this transient screen
                        Log.d(TAG, "Active session (${sessionTracker.currentChatName}) kept alive through unknown screen")
                    } else {
                        // No active session — see if we can detect a conversation
                        val possibleChat = parser.tryDetectConversation(rootInActiveWindow)
                        if (possibleChat != null) {
                            Log.d(TAG, "Fallback conversation detected on unknown screen: $possibleChat")
                            sessionTracker.startSession(possibleChat, "CHAT")
                        } else {
                            Log.d(TAG, "Could not detect a conversation on unknown screen — no session started")
                        }
                    }
                }
            }
        }
    }

    // ── Screen handlers ───────────────────────────────────────────────────────

    private fun handleChatScreen() {
        val root = rootInActiveWindow
        var title = parser.extractChatName(root)

        if (title == null) {
            Log.w(TAG, "Primary chat extraction failed — trying fallback")
            title = parser.extractChatNameFallback(root)
        }

        Log.d(TAG, "Chat name resolved: $title (current: ${sessionTracker.currentChatName})")

        when {
            title == null -> {
                // Could not resolve name — leave any active session running rather
                // than ending it, to avoid creating phantom zero-duration gaps.
                Log.w(TAG, "Could not extract chat name — leaving session unchanged")
            }
            title != sessionTracker.currentChatName -> {
                // Switched to a different (or brand-new) chat
                sessionTracker.endSession()
                sessionTracker.startSession(title, "CHAT")
                Log.d(TAG, "Chat session started: $title")
            }
            else -> {
                // Same chat still open — nothing to do
                Log.d(TAG, "Same chat still active: $title")
            }
        }
    }

    private fun handleStatusScreen() {
        val root = rootInActiveWindow
        var title = parser.extractStatusName(root)

        if (title == null) {
            Log.w(TAG, "Primary status extraction failed — trying fallback")
            title = parser.extractStatusNameFallback(root)
        }

        Log.d(TAG, "Status name resolved: $title (current: ${sessionTracker.currentChatName})")

        when {
            title == null -> {
                Log.w(TAG, "Could not extract status name — leaving session unchanged")
            }
            title != sessionTracker.currentChatName -> {
                sessionTracker.endSession()
                sessionTracker.startSession(title, "STATUS")
                Log.d(TAG, "Status session started: $title")
            }
            else -> {
                Log.d(TAG, "Same status still active: $title")
            }
        }
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted — ending session")
        sessionTracker.endSession()
    }

    override fun onDestroy() {
        Log.d(TAG, "Service destroyed — ending session")
        sessionTracker.endSession()
        serviceScope.cancel()
        super.onDestroy()
    }

    fun setDebugMode(enabled: Boolean) {
        debugMode = enabled
        Log.d(TAG, "Debug mode ${if (enabled) "enabled" else "disabled"}")
    }
}
