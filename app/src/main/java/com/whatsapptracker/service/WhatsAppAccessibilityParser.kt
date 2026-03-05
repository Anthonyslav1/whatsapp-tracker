package com.whatsapptracker.service

import android.view.accessibility.AccessibilityNodeInfo
import java.util.LinkedList
import java.util.Queue
import javax.inject.Inject

class WhatsAppAccessibilityParser @Inject constructor() {

    fun extractChatName(rootNode: AccessibilityNodeInfo?): String? {
        if (rootNode == null) return null
        return try {
            findConversationTitle(rootNode)
        } catch (e: IllegalStateException) {
            // Node was recycled
            null
        } catch (e: Exception) {
            // Log other exceptions
            e.printStackTrace()
            null
        }
    }

    fun extractStatusName(rootNode: AccessibilityNodeInfo?): String? {
        if (rootNode == null) return null
        return try {
            val title = findFirstMeaningfulTextBFS(rootNode, maxDepth = 15)
            // The first meaningful text on the status screen is usually the contact's name.
            if (title != null && !isSystemUIText(title)) title else null
        } catch (e: IllegalStateException) {
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun findConversationTitle(root: AccessibilityNodeInfo): String? {
        val toolbar = findNodeByClassNameBFS(root, "android.widget.Toolbar")
            ?: findNodeByClassNameBFS(root, "androidx.appcompat.widget.Toolbar")

        if (toolbar != null) {
            val title = findFirstMeaningfulTextBFS(toolbar)
            try { toolbar.recycle() } catch (_: Exception) {}
            if (title != null) return title
        }

        return findTitleFallbackBFS(root)
    }

    private fun findNodeByClassNameBFS(root: AccessibilityNodeInfo, className: String, maxDepth: Int = 15): AccessibilityNodeInfo? {
        val queue: Queue<Pair<AccessibilityNodeInfo, Int>> = LinkedList()
        queue.add(root to 0)

        while (queue.isNotEmpty()) {
            val (node, depth) = queue.poll()!!
            if (depth > maxDepth) continue

            if (node.className?.toString() == className) {
                return node
            }

            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                if (child != null) {
                    queue.add(child to depth + 1)
                }
            }
        }
        return null
    }

    private fun findFirstMeaningfulTextBFS(root: AccessibilityNodeInfo, maxDepth: Int = 10): String? {
        val queue: Queue<Pair<AccessibilityNodeInfo, Int>> = LinkedList()
        queue.add(root to 0)

        while (queue.isNotEmpty()) {
            val (node, depth) = queue.poll()!!
            if (depth > maxDepth) continue

            if (node.className?.toString() == "android.widget.TextView") {
                val text = node.text?.toString()
                if (text != null && text.length in 1..60 && !isSystemUIText(text)) {
                    return text
                }
            }

            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                if (child != null) {
                    queue.add(child to depth + 1)
                }
            }
        }
        return null
    }

    private fun findTitleFallbackBFS(root: AccessibilityNodeInfo): String? {
        // Fallback checks immediate children of root
        for (i in 0 until Math.min(root.childCount, 5)) {
            val child = root.getChild(i) ?: continue
            val result = findFirstMeaningfulTextBFS(child, 5)
            if (result != null) return result
        }
        return null
    }

    private fun isSystemUIText(text: String): Boolean {
        // Reduced reliance on this, but keeping as fallback.
        val lower = text.lowercase()
        val uiTexts = listOf(
            "whatsapp", "search", "back", "menu", "more options",
            "chats", "status", "calls", "communities", "updates",
            "new chat", "camera", "attach", "send", "emoji",
            "voice message", "type a message", "online", "typing",
            "last seen", "tap for more info", "click here",
            "end-to-end encrypted", "messages and calls",
            "no messages", "muted", "archived", "starred",
            "disappearing messages", "view contact", "media"
        )
        return uiTexts.any { lower.contains(it) } ||
                text.matches(Regex("\\d{1,2}:\\d{2}.*")) ||     // timestamps
                text.matches(Regex("\\d+ (messages?|unread).*")) ||  // counts
                text.matches(Regex("(today|yesterday).*", RegexOption.IGNORE_CASE))
    }
}
