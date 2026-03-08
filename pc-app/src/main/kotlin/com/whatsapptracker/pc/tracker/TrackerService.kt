package com.whatsapptracker.pc.tracker

import com.whatsapptracker.pc.db.UsageEvent
import com.whatsapptracker.pc.db.UsageEventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TrackerService {
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private val windowTracker = WindowsWindowTracker()
    private val repository = UsageEventRepository()
    
    // State
    private var isTrackingForeground = false
    private var currentSessionStart: Long = 0L
    
    var sessionTotalSeconds: Long = 0
        private set

    var isCurrentlyTracking: Boolean = false
        private set

    fun startTracking() {
        scope.launch {
            while (isActive) {
                val isForegroundNow = windowTracker.isWhatsAppForeground()
                
                if (isForegroundNow && !isTrackingForeground) {
                    // Just gained focus
                    isTrackingForeground = true
                    isCurrentlyTracking = true
                    currentSessionStart = System.currentTimeMillis()
                } else if (!isForegroundNow && isTrackingForeground) {
                    // Just lost focus
                    isTrackingForeground = false
                    isCurrentlyTracking = false
                    val sessionEnd = System.currentTimeMillis()
                    val durationSeconds = (sessionEnd - currentSessionStart) / 1000
                    
                    if (durationSeconds > 0) {
                        repository.insertEvent(
                            UsageEvent(
                                timestampStart = currentSessionStart,
                                timestampEnd = sessionEnd,
                                durationSeconds = durationSeconds
                            )
                        )
                    }
                }
                
                if (isForegroundNow) {
                    sessionTotalSeconds++
                }
                
                delay(1000) // Poll every 1 second
            }
        }
    }
}
