package com.whatsapptracker.pc.tracker

import com.whatsapptracker.pc.db.UsageEventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TrackerService(
    private val repository: UsageEventRepository
) {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)
    private val windowTracker = WindowsWindowTracker()

    // State
    private var isTrackingForeground = false

    var currentSessionStartMs: Long = 0L
        private set

    var isCurrentlyTracking: Boolean = false
        private set

    var currentChatName: String? = null
        private set

    private var lastAutosaveMs: Long = 0L
    private val autosaveIntervalMs = 30_000L // 30 seconds

    fun startTracking() {
        scope.launch {
            while (isActive) {
                val isForegroundNow = windowTracker.isWhatsAppForeground()
                val detectedChatName = windowTracker.getActiveChatName()

                if (isForegroundNow && !isTrackingForeground) {
                    // Just gained focus — start new session
                    startNewSession(detectedChatName)
                } 
                else if (!isForegroundNow && isTrackingForeground) {
                    // Just lost focus — save final session
                    saveSubSession(System.currentTimeMillis())
                    endSession()
                } 
                else if (isForegroundNow && isTrackingForeground) {
                    // App is still in focus. Check if the chat switched.
                    if (currentChatName != detectedChatName) {
                        saveSubSession(System.currentTimeMillis())
                        startNewSession(detectedChatName)
                    } else {
                        // Same chat. Check autosave.
                        val now = System.currentTimeMillis()
                        if (now - lastAutosaveMs >= autosaveIntervalMs) {
                            saveSubSession(now)
                        }
                    }
                }

                delay(1000)
            }
        }
    }

    private fun startNewSession(chatName: String?) {
        isTrackingForeground = true
        isCurrentlyTracking = true
        currentChatName = chatName
        currentSessionStartMs = System.currentTimeMillis()
        lastAutosaveMs = currentSessionStartMs
    }

    private fun endSession() {
        isTrackingForeground = false
        isCurrentlyTracking = false
        currentChatName = null
        currentSessionStartMs = 0L
    }

    private fun saveSubSession(endMs: Long) {
        val durationSeconds = (endMs - lastAutosaveMs) / 1000
        
        // Prevent DB spam from rapid micro-clicks between chats
        if (durationSeconds >= 2) {
            repository.upsertEvent(lastAutosaveMs, currentChatName, durationSeconds)
        }
        
        lastAutosaveMs = endMs
    }

    /**
     * Flush the active session to DB and cancel the coroutine.
     * Call this on app shutdown to prevent data loss.
     */
    fun stop() {
        if (isTrackingForeground) {
            saveSubSession(System.currentTimeMillis())
            endSession()
        }
        job.cancel()
    }

    /**
     * Returns the live duration of the current session in seconds.
     * Returns 0 if no session is active.
     */
    fun getLiveSessionSeconds(): Long {
        if (!isCurrentlyTracking || currentSessionStartMs == 0L) return 0L
        return (System.currentTimeMillis() - currentSessionStartMs) / 1000
    }
}
