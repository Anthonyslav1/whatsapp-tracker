package com.whatsapptracker.service

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import java.util.LinkedList
import java.util.Queue
import javax.inject.Inject

class WhatsAppAccessibilityParser @Inject constructor() {

    companion object {
        private const val TAG = "WhatsAppParser"

        // Resource IDs that hold the conversation contact name in the open-chat toolbar
        private val CHAT_TITLE_RESOURCE_IDS = listOf(
            "com.whatsapp:id/conversation_title",
            "com.whatsapp:id/toolbar_title",
            "com.whatsapp:id/action_bar_title",
            "com.whatsapp:id/contact_name"
        )

        // Resource IDs that hold the contact name in the status viewer
        private val STATUS_NAME_RESOURCE_IDS = listOf(
            "com.whatsapp:id/status_name",
            "com.whatsapp:id/status_contact_name",
            "com.whatsapp:id/contact_name",
            "com.whatsapp:id/status_caption_name",
            "com.whatsapp:id/name"
        )

        // Resource IDs that should NEVER be treated as a contact name
        private val IGNORED_RESOURCE_IDS = listOf(
            "com.whatsapp:id/conversations_row_contact_name",
            "com.whatsapp:id/conversation_contact_name",
            "com.whatsapp:id/tab",
            "com.whatsapp:id/menuitem",
            "com.whatsapp:id/search",
            "com.whatsapp:id/message_text",
            "com.whatsapp:id/caption_text",
            "com.whatsapp:id/status_timestamp",
            "com.whatsapp:id/date",
            "com.whatsapp:id/time"
        )
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    fun extractChatName(rootNode: AccessibilityNodeInfo?): String? {
        if (rootNode == null) {
            Log.w(TAG, "extractChatName: root node is null")
            return null
        }
        return try {
            val result = findConversationTitle(rootNode)
            Log.d(TAG, "extractChatName primary result: $result")
            result
        } catch (e: IllegalStateException) {
            Log.w(TAG, "extractChatName: node recycled during primary extraction", e)
            null
        }
    }

    fun extractChatNameFallback(rootNode: AccessibilityNodeInfo?): String? {
        if (rootNode == null) return null
        return try {
            Log.d(TAG, "extractChatNameFallback: attempting")
            val result = findConversationTitleFallback(rootNode)
            Log.d(TAG, "extractChatNameFallback result: $result")
            result
        } catch (e: IllegalStateException) {
            Log.w(TAG, "extractChatNameFallback: node recycled", e)
            null
        }
    }

    fun extractStatusName(rootNode: AccessibilityNodeInfo?): String? {
        if (rootNode == null) {
            Log.w(TAG, "extractStatusName: root node is null")
            return null
        }
        return try {
            // 1. Try specific resource IDs first — most reliable
            val byId = findTextByResourceIdsList(rootNode, STATUS_NAME_RESOURCE_IDS)
            if (byId != null) {
                Log.d(TAG, "extractStatusName via resource ID: $byId")
                return byId
            }

            // 2. Try the toolbar area
            val toolbar = findToolbar(rootNode)
            if (toolbar != null) {
                val fromToolbar = findFirstMeaningfulTextBFS(toolbar)
                if (fromToolbar != null) {
                    Log.d(TAG, "extractStatusName via toolbar: $fromToolbar")
                    return fromToolbar
                }
            }

            // 3. BFS from root with strict filtering — last resort
            val fromRoot = findFirstMeaningfulTextBFS(rootNode, maxDepth = 20)
            Log.d(TAG, "extractStatusName via root BFS: $fromRoot")
            if (fromRoot != null && !isSystemUIText(fromRoot)) fromRoot else null
        } catch (e: IllegalStateException) {
            Log.w(TAG, "extractStatusName: node recycled", e)
            null
        }
    }

    fun extractStatusNameFallback(rootNode: AccessibilityNodeInfo?): String? {
        if (rootNode == null) return null
        return try {
            Log.d(TAG, "extractStatusNameFallback: attempting")
            val result = findStatusNameFallback(rootNode)
            Log.d(TAG, "extractStatusNameFallback result: $result")
            result
        } catch (e: IllegalStateException) {
            Log.w(TAG, "extractStatusNameFallback: node recycled", e)
            null
        }
    }

    fun tryDetectConversation(rootNode: AccessibilityNodeInfo?): String? {
        if (rootNode == null) return null
        return try {
            Log.d(TAG, "tryDetectConversation: attempting generic detection")
            val names = findPossibleContactNames(rootNode)
            val best = names.firstOrNull { it.length in 2..60 && !isSystemUIText(it) }
            Log.d(TAG, "tryDetectConversation result: $best")
            best
        } catch (e: IllegalStateException) {
            Log.w(TAG, "tryDetectConversation: node recycled", e)
            null
        }
    }

    // -------------------------------------------------------------------------
    // Chat title extraction
    // -------------------------------------------------------------------------

    private fun findConversationTitle(root: AccessibilityNodeInfo): String? {
        Log.d(TAG, "findConversationTitle: starting")

        // ── Step 1: Resource ID lookup (most reliable across WhatsApp versions) ──
        val byId = findTextByResourceIdsList(root, CHAT_TITLE_RESOURCE_IDS)
        if (byId != null) {
            Log.d(TAG, "findConversationTitle: found via resource ID: $byId")
            return byId
        }

        // ── Step 2: Toolbar BFS ──
        val toolbar = findToolbar(root)
        if (toolbar != null) {
            Log.d(TAG, "findConversationTitle: found toolbar ${toolbar.className}")
            val fromToolbar = findFirstMeaningfulTextBFS(toolbar)
            if (fromToolbar != null) {
                Log.d(TAG, "findConversationTitle: found in toolbar: $fromToolbar")
                return fromToolbar
            }
        }

        // ── Step 3: ActionBar fallback ──
        val actionBar = findNodeByClassNameBFS(root, "android.widget.ActionBar")
        if (actionBar != null) {
            val fromActionBar = findFirstMeaningfulTextBFS(actionBar)
            if (fromActionBar != null) {
                Log.d(TAG, "findConversationTitle: found in ActionBar: $fromActionBar")
                return fromActionBar
            }
        }

        // ── Step 4: Any large/prominent text (filtered strictly) ──
        val large = findLargeTextBFS(root)
        if (large != null) {
            Log.d(TAG, "findConversationTitle: found via large-text scan: $large")
            return large
        }

        Log.d(TAG, "findConversationTitle: no title found")
        return null
    }

    private fun findConversationTitleFallback(root: AccessibilityNodeInfo): String? {
        Log.d(TAG, "findConversationTitleFallback: using fallback strategies")

        val strategies: List<() -> String?> = listOf(
            { findTextByResourceIdsList(root, CHAT_TITLE_RESOURCE_IDS) },
            { findHeaderText(root) },
            { findTextInTopArea(root) },
            { findAnyContactName(root) }
        )

        strategies.forEachIndexed { index, strategy ->
            try {
                val result = strategy()
                if (result != null) {
                    Log.d(TAG, "findConversationTitleFallback: strategy $index succeeded: $result")
                    return result
                }
            } catch (e: Exception) {
                Log.w(TAG, "findConversationTitleFallback: strategy $index threw", e)
            }
        }
        return null
    }

    // -------------------------------------------------------------------------
    // Status name extraction
    // -------------------------------------------------------------------------

    private fun findStatusNameFallback(root: AccessibilityNodeInfo): String? {
        Log.d(TAG, "findStatusNameFallback: using fallback strategies")

        val strategies: List<() -> String?> = listOf(
            { findTextByResourceIdsList(root, STATUS_NAME_RESOURCE_IDS) },
            { findTextInStatusViewer(root) },
            { findTextInCenterArea(root) }
        )

        strategies.forEachIndexed { index, strategy ->
            try {
                val result = strategy()
                if (result != null && !isSystemUIText(result)) {
                    Log.d(TAG, "findStatusNameFallback: strategy $index succeeded: $result")
                    return result
                }
            } catch (e: Exception) {
                Log.w(TAG, "findStatusNameFallback: strategy $index threw", e)
            }
        }
        return null
    }

    // -------------------------------------------------------------------------
    // Node finders
    // -------------------------------------------------------------------------

    private fun findToolbar(root: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        return findNodeByClassNameBFS(root, "android.widget.Toolbar")
            ?: findNodeByClassNameBFS(root, "androidx.appcompat.widget.Toolbar")
            ?: findNodeByClassNameBFS(root, "androidx.appcompat.widget.ActionBar")
            ?: findNodeByClassNameBFS(root, "com.android.internal.widget.ActionBarContainer")
    }

    private fun findNodeByClassNameBFS(
        root: AccessibilityNodeInfo,
        className: String,
        maxDepth: Int = 25
    ): AccessibilityNodeInfo? {
        val queue: Queue<Pair<AccessibilityNodeInfo, Int>> = LinkedList()
        queue.add(root to 0)

        while (queue.isNotEmpty()) {
            val (node, depth) = queue.poll()!!
            if (depth > maxDepth) continue

            if (node.className?.toString() == className) {
                Log.d(TAG, "findNodeByClassNameBFS: found $className at depth $depth")
                return node
            }

            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it to depth + 1) }
            }
        }
        return null
    }

    /**
     * BFS through [root] returning the first TextView text that passes
     * [isSystemUIText].  Used inside a constrained subtree (e.g. toolbar),
     * so the first hit is almost always the contact name.
     */
    private fun findFirstMeaningfulTextBFS(
        root: AccessibilityNodeInfo,
        maxDepth: Int = 20
    ): String? {
        val queue: Queue<Pair<AccessibilityNodeInfo, Int>> = LinkedList()
        queue.add(root to 0)

        while (queue.isNotEmpty()) {
            val (node, depth) = queue.poll()!!
            if (depth > maxDepth) continue

            if (node.className?.toString() == "android.widget.TextView") {
                val text = node.text?.toString()?.trim()
                if (!text.isNullOrBlank() &&
                    text.length in 1..80 &&
                    !isSystemUIText(text, node)
                ) {
                    return text
                }
            }

            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it to depth + 1) }
            }
        }
        return null
    }

    private fun findLargeTextBFS(root: AccessibilityNodeInfo): String? {
        val allTexts = mutableListOf<Pair<String, AccessibilityNodeInfo>>()
        collectAllTextNodes(root, allTexts, maxDepth = 20)

        return allTexts.firstOrNull { (text, node) ->
            text.length in 2..60 &&
                !isSystemUIText(text, node) &&
                !text.contains(":") &&          // skip timestamps like "3:45"
                !text.lowercase().contains("online")
        }?.first
    }

    private fun findPossibleContactNames(root: AccessibilityNodeInfo): List<String> {
        val names = mutableListOf<String>()
        collectAllTexts(root, names, maxDepth = 15)
        return names.filter { text ->
            text.length in 2..60 &&
                !isSystemUIText(text) &&
                !text.lowercase().contains("whatsapp") &&
                !text.trimStart().startsWith("+") &&   // not a phone number
                !text.matches(Regex("^\\d+$"))          // not purely numeric
        }
    }

    // ── Fallback helper strategies ────────────────────────────────────────────

    private fun findHeaderText(root: AccessibilityNodeInfo): String? =
        findTextInArea(root, 0.0, 0.25)

    private fun findTextInTopArea(root: AccessibilityNodeInfo): String? =
        findTextInArea(root, 0.0, 0.3)

    private fun findTextInStatusViewer(root: AccessibilityNodeInfo): String? =
        findTextByClassName(root, "android.widget.TextView", maxDepth = 20)
            ?.firstOrNull { !isSystemUIText(it) && it.length in 2..60 }

    private fun findTextInCenterArea(root: AccessibilityNodeInfo): String? =
        findTextInArea(root, 0.3, 0.7)

    private fun findAnyContactName(root: AccessibilityNodeInfo): String? {
        val allTexts = mutableListOf<String>()
        collectAllTexts(root, allTexts, maxDepth = 15)
        return allTexts.firstOrNull { text ->
            text.length in 2..60 &&
                !isSystemUIText(text) &&
                !text.lowercase().contains("whatsapp") &&
                !text.trimStart().startsWith("+") &&
                !text.matches(Regex("^\\d+$"))
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun findTextInArea(
        root: AccessibilityNodeInfo,
        startFraction: Double,
        endFraction: Double
    ): String? = findFirstMeaningfulTextBFS(root, maxDepth = 15)

    // ── Resource-ID based lookup ──────────────────────────────────────────────

    private fun findTextByResourceIdsList(
        root: AccessibilityNodeInfo,
        resourceIds: List<String>
    ): String? {
        val queue: Queue<AccessibilityNodeInfo> = LinkedList()
        queue.add(root)

        while (queue.isNotEmpty()) {
            val node = queue.poll()!!
            val resId = node.viewIdResourceName
            if (resId != null && resourceIds.any { resId == it || resId.endsWith(it.substringAfter(":id/").let { s -> ":id/$s" }) }) {
                val text = node.text?.toString()?.trim()
                if (!text.isNullOrBlank() && !isSystemUIText(text, node)) {
                    return text
                }
            }
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }
        return null
    }

    // ── Collection helpers ────────────────────────────────────────────────────

    private fun collectAllTexts(
        node: AccessibilityNodeInfo,
        texts: MutableList<String>,
        maxDepth: Int = 15,
        currentDepth: Int = 0
    ) {
        if (currentDepth > maxDepth) return
        if (node.className?.toString() == "android.widget.TextView") {
            node.text?.toString()?.trim()?.let { if (it.isNotBlank()) texts.add(it) }
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { collectAllTexts(it, texts, maxDepth, currentDepth + 1) }
        }
    }

    private fun collectAllTextNodes(
        node: AccessibilityNodeInfo,
        texts: MutableList<Pair<String, AccessibilityNodeInfo>>,
        maxDepth: Int = 20,
        currentDepth: Int = 0
    ) {
        if (currentDepth > maxDepth) return
        if (node.className?.toString() == "android.widget.TextView") {
            node.text?.toString()?.trim()?.let { if (it.isNotBlank()) texts.add(it to node) }
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { collectAllTextNodes(it, texts, maxDepth, currentDepth + 1) }
        }
    }

    private fun findTextByClassName(
        root: AccessibilityNodeInfo,
        className: String,
        maxDepth: Int = 20
    ): List<String>? {
        val texts = mutableListOf<String>()
        val queue: Queue<Pair<AccessibilityNodeInfo, Int>> = LinkedList()
        queue.add(root to 0)

        while (queue.isNotEmpty()) {
            val (node, depth) = queue.poll()!!
            if (depth > maxDepth) continue
            if (node.className?.toString() == className) {
                node.text?.toString()?.trim()?.let { if (it.isNotBlank()) texts.add(it) }
            }
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it to depth + 1) }
            }
        }
        return texts.ifEmpty { null }
    }

    // -------------------------------------------------------------------------
    // isSystemUIText — central filter
    // -------------------------------------------------------------------------

    /**
     * Returns true if [text] is WhatsApp UI chrome, a message body snippet,
     * a timestamp, or any other string that is NOT a contact/group name.
     */
    private fun isSystemUIText(
        text: String,
        node: AccessibilityNodeInfo? = null
    ): Boolean {
        // ── Resource-ID block list ────────────────────────────────────────────
        val resName = node?.viewIdResourceName
        if (resName != null && IGNORED_RESOURCE_IDS.any { resName == it || resName.contains(it.substringAfterLast("/")) }) {
            return true
        }

        val raw = text.trim()
        val lower = raw.lowercase()

        // ── Length guards ─────────────────────────────────────────────────────
        if (lower.length < 2) return true
        if (lower.length > 80) return true   // almost certainly message content

        // ── Timestamp / clock patterns  e.g. "3:45", "12:30 PM", "Yesterday 3:45" ──
        if (lower.matches(Regex("\\d{1,2}:\\d{2}(\\s*(am|pm))?(\\s.*)?", RegexOption.IGNORE_CASE))) return true
        if (lower.matches(Regex("(today|yesterday)(,?\\s.*)?", RegexOption.IGNORE_CASE))) return true

        // ── Status / availability strings ─────────────────────────────────────
        if (lower.matches(Regex(".*(\\bonline\\b|typing\\.\\.\\.|recording\\.\\.\\.|available).*"))) return true
        if (lower.matches(Regex("last seen .+"))) return true

        // ── Message-count badges ──────────────────────────────────────────────
        if (lower.matches(Regex("\\d+ (messages?|unread).*"))) return true

        // ── Deleted-message placeholders ──────────────────────────────────────
        if (lower.contains("this message was deleted")) return true
        if (lower.contains("you deleted this message")) return true
        if (lower.contains("message was deleted")) return true

        // ── Media-type labels (appear as last-message previews in the toolbar) ─
        if (lower.matches(Regex("(📷\\s*)?(photo)(\\s.*)?$"))) return true
        if (lower.matches(Regex("(🎥\\s*)?(video)(\\s.*)?$"))) return true
        if (lower.matches(Regex("(🎞\\s*)?(gif)(\\s.*)?$"))) return true
        if (lower.matches(Regex("(🔊\\s*)?(voice message|audio)(\\s.*)?$"))) return true
        if (lower.matches(Regex("(🎵\\s*)?(audio message)(\\s.*)?$"))) return true
        if (lower.matches(Regex("(😄\\s*)?(sticker)(\\s.*)?$"))) return true
        if (lower.matches(Regex("(📄\\s*)?(document)(\\s.*)?$"))) return true
        if (lower.matches(Regex("(📍\\s*)?(location)(\\s.*)?$"))) return true
        if (lower.matches(Regex("(👤\\s*)?(contact)(\\s.*)?$"))) return true
        // Generic emoji-prefixed media previews
        if (lower.matches(Regex("^[\\p{So}\\p{Sm}\\p{Sk}\\p{Sc}]\\s*(photo|video|audio|sticker|gif|document|voice)(\\s.*)?$"))) return true

        // ── Call-related strings ──────────────────────────────────────────────
        if (lower.matches(Regex("(missed )?(voice |video )?call(\\s.*)?$"))) return true
        if (lower.matches(Regex("(incoming|outgoing) (voice |video )?call(\\s.*)?$"))) return true

        // ── WhatsApp system / info messages ──────────────────────────────────
        if (lower.contains("end-to-end encrypted")) return true
        if (lower.contains("messages and calls are")) return true
        if (lower.contains("tap to view")) return true
        if (lower.contains("view once")) return true
        if (lower.contains("viewed once")) return true
        if (lower.contains("disappearing messages")) return true
        if (lower.contains("you created this group")) return true
        if (lower.contains("added you")) return true
        if (lower.contains("changed the subject")) return true
        if (lower.contains("security code changed")) return true
        if (lower.contains("you can now call")) return true

        // ── Status-viewer time indicators ────────────────────────────────────
        if (lower == "just now") return true
        if (lower.matches(Regex("\\d+ (sec|secs|second|seconds|min|mins|minute|minutes|hr|hrs|hour|hours) ago(\\s.*)?$"))) return true

        // ── Group / participant metadata ──────────────────────────────────────
        if (lower.matches(Regex("\\d+ participant.*"))) return true
        if (lower == "admin") return true
        if (lower == "muted") return true

        // ── App name and generic UI strings ──────────────────────────────────
        if (lower == "whatsapp") return true
        if (lower.contains("touch the fingerprint sensor")) return true
        if (lower.contains("ask meta ai")) return true
        if (lower == "search...") return true
        if (lower == "search") return true
        if (lower == "chats") return true
        if (lower == "updates") return true
        if (lower == "calls") return true
        if (lower == "communities") return true
        if (lower == "settings") return true

        return false
    }
}
