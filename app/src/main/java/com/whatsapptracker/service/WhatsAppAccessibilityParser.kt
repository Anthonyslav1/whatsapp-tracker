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
        }
    }

    fun extractStatusName(rootNode: AccessibilityNodeInfo?): String? {
        if (rootNode == null) return null
        return try {
            val title = findFirstMeaningfulTextBFS(rootNode, maxDepth = 15)
            // The first meaningful text on the status screen is usually the contact's name.
            if (title != null && !isSystemUIText(title, rootNode)) title else null
        } catch (e: IllegalStateException) {
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

        return null // Fail fast, do not fallback to full root node search
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
                if (text != null && text.length in 1..60 && !isSystemUIText(text, node)) {
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

    private fun isSystemUIText(text: String, node: AccessibilityNodeInfo? = null): Boolean {
        // Option 1: Ignore known WhatsApp system UI resource IDs
        val resName = node?.viewIdResourceName
        if (resName != null) {
            val ignoredIds = listOf(
                "com.whatsapp:id/conversation_contact_name", // We want the toolbar title, not list items
                "com.whatsapp:id/conversations_row_contact_name",
                "com.whatsapp:id/tab",
                "com.whatsapp:id/menuitem",
                "com.whatsapp:id/search"
            )
            if (ignoredIds.any { resName.contains(it) }) return true
        }

        // Option 2: Regex for common non-name patterns
        val lower = text.lowercase()
        return lower.matches(Regex(".*(online|typing...).*")) ||
               lower.matches(Regex("\\d{1,2}:\\d{2}.*")) ||     // timestamps
               lower.matches(Regex("\\d+ (messages?|unread).*")) ||  // counts
               lower.matches(Regex("(today|yesterday).*", RegexOption.IGNORE_CASE)) ||
               lower.length < 2 // Ignore 1 letter artifacts
    }
}
