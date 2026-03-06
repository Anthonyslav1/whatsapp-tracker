package com.whatsapptracker.service

import android.view.accessibility.AccessibilityNodeInfo
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class WhatsAppAccessibilityParserTest {

    private lateinit var parser: WhatsAppAccessibilityParser

    @Before
    fun setUp() {
        parser = WhatsAppAccessibilityParser()
    }

    @Test
    fun testNullRootNode() {
        assertNull(parser.extractChatName(null))
    }

    @Test
    fun testFindConversationTitleInToolbar() {
        // Mocking AccessibilityNodeInfo is tricky without Robolectric, 
        // but since we are mocking it via MockK, we can simulate the tree.
        val rootNode = mockk<AccessibilityNodeInfo>(relaxed = true)
        val toolbarNode = mockk<AccessibilityNodeInfo>(relaxed = true)
        val titleNode = mockk<AccessibilityNodeInfo>(relaxed = true)

        every { rootNode.childCount } returns 1
        every { rootNode.getChild(0) } returns toolbarNode
        every { toolbarNode.className } returns "androidx.appcompat.widget.Toolbar"
        every { toolbarNode.childCount } returns 1
        every { toolbarNode.getChild(0) } returns titleNode
        
        every { titleNode.className } returns "android.widget.TextView"
        every { titleNode.text } returns "John Doe"
        every { titleNode.viewIdResourceName } returns "com.whatsapp:id/conversation_contact_name_title"
        every { titleNode.childCount } returns 0

        val result = parser.extractChatName(rootNode)
        assertEquals("John Doe", result)
    }

    @Test
    fun testSystemUITextIsIgnored() {
        val rootNode = mockk<AccessibilityNodeInfo>(relaxed = true)
        val toolbarNode = mockk<AccessibilityNodeInfo>(relaxed = true)
        val uiTextNode = mockk<AccessibilityNodeInfo>(relaxed = true)

        every { rootNode.childCount } returns 1
        every { rootNode.getChild(0) } returns toolbarNode
        every { toolbarNode.className } returns "androidx.appcompat.widget.Toolbar"
        every { toolbarNode.childCount } returns 1
        every { toolbarNode.getChild(0) } returns uiTextNode
        
        every { uiTextNode.className } returns "android.widget.TextView"
        every { uiTextNode.text } returns "Typing..." // System string matched by Regex
        every { uiTextNode.viewIdResourceName } returns "com.whatsapp:id/conversation_contact_status"
        every { uiTextNode.childCount } returns 0

        val result = parser.extractChatName(rootNode)
        assertNull(result)
    }

    @Test
    fun testExtractStatusNameWithDeepTree() {
        // Create a deep tree (depth 5) to test BFS performance and correctness.
        val rootNode = mockk<AccessibilityNodeInfo>(relaxed = true)
        var currentNode = rootNode
        
        for (i in 1..4) {
            val childNode = mockk<AccessibilityNodeInfo>(relaxed = true)
            every { currentNode.childCount } returns 2
            
            // Add a decoy text node that should NOT be picked up (e.g., system UI text)
            val decoyNode = mockk<AccessibilityNodeInfo>(relaxed = true)
            every { decoyNode.className } returns "android.widget.TextView"
            every { decoyNode.text } returns "2 mutual friends" // Meaningful text, but...
            // Wait, "2 mutual friends" WOULD be picked up if it's the first text found on a status screen!
            // Actually, we want to test status. Status screens have timestamps like "10:45 AM"
            every { decoyNode.text } returns "10:45" // Timestamps are ignored by isSystemUIText
            every { decoyNode.childCount } returns 0
            
            every { currentNode.getChild(0) } returns decoyNode
            every { currentNode.getChild(1) } returns childNode
            
            currentNode = childNode
        }
        
        // At depth 5, put the actual status creator's name
        val targetNode = mockk<AccessibilityNodeInfo>(relaxed = true)
        every { targetNode.className } returns "android.widget.TextView"
        every { targetNode.text } returns "Alice Wonderland"
        every { targetNode.childCount } returns 0
        every { targetNode.viewIdResourceName } returns "com.whatsapp:id/contact_name"
        
        every { currentNode.childCount } returns 1
        every { currentNode.getChild(0) } returns targetNode
        
        val result = parser.extractStatusName(rootNode)
        assertEquals("Alice Wonderland", result)
    }

    @Test
    fun testExtractStatusNameReturnsNullForOnlySystemUI() {
        val rootNode = mockk<AccessibilityNodeInfo>(relaxed = true)
        val targetNode = mockk<AccessibilityNodeInfo>(relaxed = true)
        
        every { targetNode.className } returns "android.widget.TextView"
        every { targetNode.text } returns "Click here" // Ignored by isSystemUIText
        every { targetNode.childCount } returns 0
        every { targetNode.viewIdResourceName } returns "com.whatsapp:id/status_text"
        
        every { rootNode.childCount } returns 1
        every { rootNode.getChild(0) } returns targetNode
        
        val result = parser.extractStatusName(rootNode)
        assertNull(result)
    }
}
