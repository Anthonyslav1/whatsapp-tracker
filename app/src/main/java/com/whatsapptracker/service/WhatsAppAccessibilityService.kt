package com.whatsapptracker.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Inject

@AndroidEntryPoint
class WhatsAppAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var parser: WhatsAppAccessibilityParser

    @Inject
    lateinit var sessionTracker: SessionTrackerManager

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onServiceConnected() {
        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        val className = event.className?.toString() ?: return

        // Ignore system UI events
        if (className.contains("$") || packageName == "android") return

        if (packageName == "com.whatsapp" || packageName == "com.whatsapp.w4b") {
            handleWhatsAppScreen(className)
        } else if (sessionTracker.currentChatName != null) {
            // User left WhatsApp
            sessionTracker.endSession()
        }
    }

    private fun handleWhatsAppScreen(className: String) {
        when {
            // Chat conversation detected
            className.contains("Conversation", ignoreCase = true) ||
            className.contains("ConversationActivity", ignoreCase = true) -> {
                serviceScope.launch {
                    delay(150)
                    val title = parser.extractChatName(rootInActiveWindow)
                    if (title != null && title != sessionTracker.currentChatName) {
                        sessionTracker.endSession()
                        sessionTracker.startSession(title, "CHAT")
                    }
                }
            }
            // Status viewing detected
            className.contains("StatusPlaybackActivity", ignoreCase = true) ||
            className.contains("StatusViewer", ignoreCase = true) -> {
                serviceScope.launch {
                    delay(150)
                    val title = parser.extractStatusName(rootInActiveWindow)
                    if (title != null && title != sessionTracker.currentChatName) {
                        sessionTracker.endSession()
                        sessionTracker.startSession(title, "STATUS")
                    }
                }
            }
            // Home screen or other WhatsApp screens
            className.contains("HomeActivity", ignoreCase = true) ||
            className.contains("Main", ignoreCase = true) ||
            className.contains("Settings", ignoreCase = true) ||
            className.contains("Profile", ignoreCase = true) ||
            className.contains("About", ignoreCase = true) -> {
                sessionTracker.endSession()
            }
        }
    }

    override fun onInterrupt() {
        sessionTracker.endSession()
    }

    override fun onDestroy() {
        sessionTracker.endSession()
        serviceScope.cancel()
        super.onDestroy()
    }
}
