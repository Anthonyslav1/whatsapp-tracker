package com.whatsapptracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactName: String,
    val startTime: Long,
    val endTime: Long = 0,
    val durationMs: Long = 0,
    val sessionType: String = "CHAT" // "CHAT" for v1, "STATUS_VIEW" for v2
)
