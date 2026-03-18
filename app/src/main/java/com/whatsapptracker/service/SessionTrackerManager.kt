package com.whatsapptracker.service

import android.util.Log
import com.whatsapptracker.data.db.ChatSession
import com.whatsapptracker.data.db.ChatSessionDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionTrackerManager @Inject constructor(
    private val dao: ChatSessionDao
) {
    companion object {
        private const val TAG = "SessionTracker"
        // Minimum duration to save — filters out accidental taps / screen flashes
        private const val MIN_SESSION_DURATION_MS = 2000L
    }

    // Isolated IO scope so DB writes survive service interruption
    private val trackerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Public read, private write
    var currentChatName: String? = null
        private set

    private var currentSessionType: String = "CHAT"
    private var sessionStartTime: Long = 0L

    /**
     * Begin tracking a new session.
     * If a session is already active it is ended first.
     */
    fun startSession(chatName: String, sessionType: String = "CHAT") {
        Log.d(TAG, "startSession called: chatName=$chatName type=$sessionType")

        // Flush any in-progress session before starting a new one
        if (currentChatName != null) {
            Log.w(TAG, "Active session exists (${currentChatName}), ending it first")
            endSession()
        }

        currentChatName = chatName
        currentSessionType = sessionType
        sessionStartTime = System.currentTimeMillis()

        Log.d(TAG, "Session started: $chatName at $sessionStartTime")
    }

    /**
     * End the current session and persist it to the database.
     *
     * The full ChatSession row (including endTime and durationMs) is only
     * written HERE — never at startSession time.  This eliminates the previous
     * race condition where endSession() could fire before the async INSERT in
     * startSession() completed, causing currentSessionId to be null and the
     * duration update to be silently dropped.
     */
    fun endSession() {
        val name = currentChatName ?: run {
            Log.d(TAG, "endSession called but no active session — ignoring")
            return
        }

        val type = currentSessionType
        val startTime = sessionStartTime
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        Log.d(TAG, "Ending session: name=$name type=$type duration=${duration}ms")

        // Clear state BEFORE launching async work so any immediate re-entry
        // (e.g. startSession called right after endSession on the main thread)
        // sees a clean slate and does not double-end.
        currentChatName = null
        currentSessionType = "CHAT"
        sessionStartTime = 0L

        if (duration < MIN_SESSION_DURATION_MS) {
            Log.d(TAG, "Session too short (${duration}ms < ${MIN_SESSION_DURATION_MS}ms) — discarding")
            return
        }

        trackerScope.launch {
            try {
                val session = ChatSession(
                    contactName = name,
                    startTime = startTime,
                    endTime = endTime,
                    durationMs = duration,
                    sessionType = type
                )
                val id = dao.insert(session)
                Log.d(TAG, "Session saved: id=$id contact=$name duration=${duration}ms")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save session for $name", e)
            }
        }
    }

    /**
     * Returns true when a valid session is currently being tracked.
     */
    fun validateSessionState(): Boolean {
        val active = currentChatName != null && sessionStartTime > 0L
        Log.d(TAG, "validateSessionState: active=$active name=$currentChatName start=$sessionStartTime")
        return active
    }
}
