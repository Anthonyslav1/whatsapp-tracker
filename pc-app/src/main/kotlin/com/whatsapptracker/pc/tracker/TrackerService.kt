package com.whatsapptracker.pc.tracker

import com.whatsapptracker.pc.db.UsageEvent
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

    private var lastAutosaveMs: Long = 0L
    private val autosaveIntervalMs = 30_000L // 30 seconds

    fun startTracking() {
        scope.launch {
            while (isActive) {
                val isForegroundNow = windowTracker.isWhatsAppForeground()

                if (isForegroundNow && !isTrackingForeground) {
                    // Just gained focus — start new session
                    isTrackingForeground = true
                    isCurrentlyTracking = true
                    currentSessionStartMs = System.currentTimeMillis()
                    lastAutosaveMs = currentSessionStartMs
                } else if (!isForegroundNow && isTrackingForeground) {
                    // Just lost focus — save final session
                    isTrackingForeground = false
                    isCurrentlyTracking = false
                    saveSession(currentSessionStartMs, System.currentTimeMillis())
                    currentSessionStartMs = 0L
                } else if (isForegroundNow && isTrackingForeground) {
                    // Still active — check autosave
                    val now = System.currentTimeMillis()
                    if (now - lastAutosaveMs >= autosaveIntervalMs) {
                        // Save a partial session from last save point to now
                        saveSession(lastAutosaveMs, now)
                        lastAutosaveMs = now
                    }
                }

                delay(1000)
            }
        }
    }

    /**
     * Flush the active session to DB and cancel the coroutine.
     * Call this on app shutdown to prevent data loss.
     */
    fun stop() {
        if (isTrackingForeground) {
            val now = System.currentTimeMillis()
            // Only save the portion since the last autosave
            saveSession(lastAutosaveMs, now)
            isTrackingForeground = false
            isCurrentlyTracking = false
            currentSessionStartMs = 0L
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

    private fun saveSession(startMs: Long, endMs: Long) {
        val durationSeconds = (endMs - startMs) / 1000
        if (durationSeconds > 0) {
            repository.insertEvent(
                UsageEvent(
                    timestampStart = startMs,
                    timestampEnd = endMs,
                    durationSeconds = durationSeconds
                )
            )
        }
    }
}
