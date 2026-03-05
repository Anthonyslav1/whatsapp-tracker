package com.whatsapptracker.service

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
    // Isolated scope for DB writes so they aren't cancelled if the Service is abruptly destroyed.
    private val trackerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var currentChatName: String? = null
        private set
    private var currentSessionId: Long? = null
    private var sessionStartTime: Long = 0

    fun startSession(chatName: String, sessionType: String = "CHAT") {
        currentChatName = chatName
        sessionStartTime = System.currentTimeMillis()

        trackerScope.launch {
            val session = ChatSession(
                contactName = chatName,
                startTime = sessionStartTime,
                sessionType = sessionType
            )
            currentSessionId = dao.insert(session)
        }
    }

    fun endSession() {
        val sessionId = currentSessionId ?: return
        val endTime = System.currentTimeMillis()
        val duration = endTime - sessionStartTime

        if (duration > 2000) {
            trackerScope.launch {
                val session = dao.getById(sessionId)
                if (session != null) {
                    dao.update(session.copy(endTime = endTime, durationMs = duration))
                }
            }
        } else {
            trackerScope.launch {
                val session = dao.getById(sessionId)
                if (session != null) {
                    dao.delete(session)
                }
            }
        }

        currentChatName = null
        currentSessionId = null
        sessionStartTime = 0
    }
}
