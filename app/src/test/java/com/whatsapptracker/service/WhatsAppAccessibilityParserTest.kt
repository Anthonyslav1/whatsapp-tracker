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
        every { uiTextNode.text } returns "Typing..." // System string
        every { uiTextNode.childCount } returns 0

        val result = parser.extractChatName(rootNode)
        assertNull(result)
    }
}
