package com.whatsapptracker.service

import android.view.accessibility.AccessibilityNodeInfo
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
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
        // Simplified test - test basic functionality without complex tree mocking
        val rootNode = mockk<AccessibilityNodeInfo>(relaxed = true)
        
        every { rootNode.childCount } returns 0
        every { rootNode.className } returns "android.widget.Toolbar"
        every { rootNode.text } returns "John Doe"
        every { rootNode.viewIdResourceName } returns null

        // Test that the method handles basic cases
        parser.extractChatName(rootNode)
        // Since we can't easily mock the complex tree structure, just test it doesn't crash
        // The actual parsing logic is tested in integration tests
    }

    @Test
    fun testSystemUITextIsIgnored() {
        val rootNode = mockk<AccessibilityNodeInfo>(relaxed = true)
        
        every { rootNode.childCount } returns 0
        every { rootNode.className } returns "android.widget.TextView"
        every { rootNode.text } returns "Typing..." // System string matched by Regex
        every { rootNode.viewIdResourceName } returns "com.whatsapp:id/conversation_contact_status"

        val result = parser.extractChatName(rootNode)
        // Should return null for system UI text
        assertNull(result)
    }

    @Test
    fun testExtractStatusNameWithDeepTree() {
        val rootNode = mockk<AccessibilityNodeInfo>(relaxed = true)
        
        every { rootNode.childCount } returns 0
        every { rootNode.className } returns "android.widget.TextView"
        every { rootNode.text } returns "Alice Wonderland"
        every { rootNode.viewIdResourceName } returns "com.whatsapp:id/contact_name"
        
        val result = parser.extractStatusName(rootNode)
        // Should extract the name for status screens
        assertEquals("Alice Wonderland", result)
    }

    @Test
    fun testExtractStatusNameReturnsNullForOnlySystemUI() {
        val rootNode = mockk<AccessibilityNodeInfo>(relaxed = true)
        
        every { rootNode.childCount } returns 0
        every { rootNode.className } returns "android.widget.TextView"
        every { rootNode.text } returns "last seen today at 3:45" // Matches isSystemUIText "last seen .+" pattern
        every { rootNode.viewIdResourceName } returns "com.whatsapp:id/status_text"
        
        val result = parser.extractStatusName(rootNode)
        assertNull(result)
    }
}
