package com.whatsapptracker.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatSessionDao {

    @Insert
    suspend fun insert(session: ChatSession): Long

    @Update
    suspend fun update(session: ChatSession)

    @Delete
    suspend fun delete(session: ChatSession)

    @Query("SELECT * FROM chat_sessions WHERE id = :id")
    suspend fun getById(id: Long): ChatSession?

    @Query("""
        SELECT COALESCE(SUM(durationMs), 0) 
        FROM chat_sessions 
        WHERE startTime >= :startTime AND startTime < :endTime AND endTime > 0 AND sessionType = 'CHAT'
    """)
    fun getTotalDurationInRange(startTime: Long, endTime: Long): Flow<Long>

    @Query("""
        SELECT contactName, SUM(durationMs) as totalDuration 
        FROM chat_sessions 
        WHERE startTime >= :startTime AND startTime < :endTime AND endTime > 0 AND sessionType = 'CHAT'
        GROUP BY contactName 
        ORDER BY totalDuration DESC 
        LIMIT :limit
    """)
    fun getTopContacts(startTime: Long, endTime: Long, limit: Int): Flow<List<ContactDuration>>

    @Query("""
        SELECT * FROM chat_sessions 
        WHERE startTime >= :startTime AND startTime < :endTime AND endTime > 0 AND sessionType = 'CHAT'
        ORDER BY startTime ASC
    """)
    fun getSessionsInRange(startTime: Long, endTime: Long): Flow<List<ChatSession>>

    @Query("""
        SELECT * FROM chat_sessions 
        WHERE startTime >= :startTime AND startTime < :endTime AND endTime > 0 AND sessionType = 'CHAT'
        ORDER BY startTime ASC
    """)
    suspend fun getAllSessionsInRange(startTime: Long, endTime: Long): List<ChatSession>

    @Query("""
        SELECT * FROM chat_sessions 
        WHERE startTime >= :startTime AND startTime < :endTime AND endTime > 0 AND sessionType = 'CHAT'
        ORDER BY durationMs DESC LIMIT 1
    """)
    suspend fun getLongestSession(startTime: Long, endTime: Long): ChatSession?
    @Query("""
        SELECT cast(strftime('%m', startTime / 1000, 'unixepoch', 'localtime') as integer) as month, SUM(durationMs) as totalDuration 
        FROM chat_sessions 
        WHERE startTime >= :startTime AND startTime < :endTime AND endTime > 0 AND sessionType = 'CHAT'
        GROUP BY month 
        ORDER BY month ASC
    """)
    suspend fun getMonthlyDurations(startTime: Long, endTime: Long): List<MonthlyDuration>

    @Query("""
        SELECT cast(strftime('%w', startTime / 1000, 'unixepoch', 'localtime') as integer) as dayOfWeek, SUM(durationMs) as totalDuration
        FROM chat_sessions
        WHERE startTime >= :startTime AND startTime < :endTime AND endTime > 0 AND sessionType = 'CHAT'
        GROUP BY dayOfWeek
        ORDER BY totalDuration DESC
        LIMIT 1
    """)
    suspend fun getMostActiveDayOfWeek(startTime: Long, endTime: Long): DayOfWeekDuration?

    @Query("""
        SELECT COUNT(DISTINCT contactName) 
        FROM chat_sessions 
        WHERE startTime >= :startTime AND startTime < :endTime AND endTime > 0 AND sessionType = 'CHAT'
    """)
    suspend fun getUniqueContactCount(startTime: Long, endTime: Long): Int

    @Query("""
        SELECT COUNT(*) 
        FROM chat_sessions 
        WHERE startTime >= :startTime AND startTime < :endTime AND endTime > 0 AND sessionType = 'CHAT'
    """)
    suspend fun getTotalSessionCount(startTime: Long, endTime: Long): Int

    @Query("""
        SELECT contactName, SUM(durationMs) as totalDuration 
        FROM chat_sessions 
        WHERE startTime >= :startTime AND startTime < :endTime AND endTime > 0 AND sessionType = 'CHAT'
        GROUP BY contactName 
        ORDER BY totalDuration DESC 
        LIMIT :limit
    """)
    suspend fun getTopContactsYearly(startTime: Long, endTime: Long, limit: Int): List<ContactDuration>

    // --- STATUS QUERIES (V2) ---

    @Query("""
        SELECT contactName, SUM(durationMs) as totalDuration 
        FROM chat_sessions 
        WHERE startTime >= :startTime AND startTime < :endTime AND endTime > 0 AND sessionType = 'STATUS'
        GROUP BY contactName 
        ORDER BY totalDuration DESC 
        LIMIT :limit
    """)
    fun getTopEntertainers(startTime: Long, endTime: Long, limit: Int): Flow<List<ContactDuration>>

    @Query("""
        SELECT contactName, SUM(durationMs) as totalDuration 
        FROM chat_sessions 
        WHERE startTime >= :startTime AND startTime < :endTime AND endTime > 0 AND sessionType = 'STATUS'
        GROUP BY contactName 
        ORDER BY totalDuration DESC 
        LIMIT 1
    """)
    suspend fun getTopEntertainerYearly(startTime: Long, endTime: Long): ContactDuration?
}

data class ContactDuration(
    val contactName: String,
    val totalDuration: Long
)

data class MonthlyDuration(
    val month: Int,
    val totalDuration: Long
)

data class DayOfWeekDuration(
    val dayOfWeek: Int,
    val totalDuration: Long
)
