package com.whatsapptracker.service

import com.whatsapptracker.data.db.ChatSession
import com.whatsapptracker.data.db.ChatSessionDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Data

@Singleton
class SessionTrackerManager @Inject constructor(
    private val dao: ChatSessionDao,
    private val workManager: WorkManager
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
            val inputData = Data.Builder()
                .putLong(SessionSaveWorker.KEY_SESSION_ID, sessionId)
                .putLong(SessionSaveWorker.KEY_END_TIME, endTime)
                .putLong(SessionSaveWorker.KEY_DURATION, duration)
                .build()

            val saveWork = OneTimeWorkRequestBuilder<SessionSaveWorker>()
                .setInputData(inputData)
                .build()

            workManager.enqueue(saveWork)
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
