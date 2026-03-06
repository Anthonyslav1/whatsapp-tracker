package com.whatsapptracker.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.whatsapptracker.data.db.ChatSessionDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SessionSaveWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dao: ChatSessionDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val sessionId = inputData.getLong(KEY_SESSION_ID, -1)
        val endTime = inputData.getLong(KEY_END_TIME, -1)
        val duration = inputData.getLong(KEY_DURATION, -1)

        if (sessionId == -1L || endTime == -1L || duration == -1L) {
            return Result.failure()
        }

        return try {
            val session = dao.getById(sessionId)
            if (session != null) {
                if (duration > 2000) {
                    dao.update(session.copy(endTime = endTime, durationMs = duration))
                } else {
                    dao.delete(session) // Too short
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        const val KEY_SESSION_ID = "SESSION_ID"
        const val KEY_END_TIME = "END_TIME"
        const val KEY_DURATION = "DURATION"
    }
}
