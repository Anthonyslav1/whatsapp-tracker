package com.whatsapptracker.pc.db

import java.time.LocalDate
import java.time.ZoneId

data class UsageEvent(
    val id: Int = 0,
    val dateMs: Long,
    val chatName: String?,
    val durationSeconds: Long
)

// Aggregate class for Top Contacts
data class TopContact(
    val chatName: String?,
    val totalSeconds: Long
)

class UsageEventRepository {

    /**
     * Upserts an event into usage_events_v2.
     * Guarantees 1 row per (start-of-day, chatName).
     */
    fun upsertEvent(dateMs: Long, chatName: String?, durationSeconds: Long) {
        val startOfDayMs = getStartOfDayFor(dateMs)
        
        // SQLite 3.24.0+ supports UPSERT
        val query = """
            INSERT INTO usage_events_v2(date_ms, chat_name, duration_seconds) 
            VALUES (?, ?, ?)
            ON CONFLICT(date_ms, chat_name) 
            DO UPDATE SET duration_seconds = duration_seconds + excluded.duration_seconds
        """.trimIndent()
        
        val conn = Database.getConnection()
        conn.prepareStatement(query).use { pstmt ->
            pstmt.setLong(1, startOfDayMs)
            
            if (chatName == null) {
                pstmt.setNull(2, java.sql.Types.VARCHAR)
            } else {
                pstmt.setString(2, chatName)
            }
            
            pstmt.setLong(3, durationSeconds)
            pstmt.executeUpdate()
        }
    }

    fun getAllEvents(): List<UsageEvent> {
        return queryEvents("SELECT * FROM usage_events_v2 ORDER BY date_ms DESC")
    }

    fun getTodaySessions(): List<UsageEvent> {
        val startOfDay = startOfTodayMs()
        return queryEvents(
            "SELECT * FROM usage_events_v2 WHERE date_ms >= ? ORDER BY duration_seconds DESC",
            startOfDay
        )
    }

    fun getTodayTotalSeconds(): Long {
        val startOfDay = startOfTodayMs()
        val query = "SELECT COALESCE(SUM(duration_seconds), 0) FROM usage_events_v2 WHERE date_ms >= ?"
        val conn = Database.getConnection()
        conn.prepareStatement(query).use { pstmt ->
            pstmt.setLong(1, startOfDay)
            val rs = pstmt.executeQuery()
            if (rs.next()) return rs.getLong(1)
        }
        return 0L
    }

    fun getWeeklyTotals(): Map<LocalDate, Long> {
        val today = LocalDate.now()
        val startDate = today.minusDays(6)
        val startMs = startDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val result = mutableMapOf<LocalDate, Long>()
        for (i in 0..6) {
            result[startDate.plusDays(i.toLong())] = 0L
        }

        val query = "SELECT date_ms, SUM(duration_seconds) as dur FROM usage_events_v2 WHERE date_ms >= ? GROUP BY date_ms"
        val conn = Database.getConnection()
        conn.prepareStatement(query).use { pstmt ->
            pstmt.setLong(1, startMs)
            val rs = pstmt.executeQuery()
            while (rs.next()) {
                val ts = rs.getLong("date_ms")
                val dur = rs.getLong("dur")
                val date = java.time.Instant.ofEpochMilli(ts)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                result[date] = (result[date] ?: 0L) + dur
            }
        }
        return result
    }

    fun getTodaySessionCount(): Int {
        val startOfDay = startOfTodayMs()
        // We consider every unique chat interacted with today as a 'session' type of metric,
        // or we just return the raw row count.
        val query = "SELECT COUNT(*) FROM usage_events_v2 WHERE date_ms >= ?"
        val conn = Database.getConnection()
        conn.prepareStatement(query).use { pstmt ->
            pstmt.setLong(1, startOfDay)
            val rs = pstmt.executeQuery()
            if (rs.next()) return rs.getInt(1)
        }
        return 0
    }

    /**
     * Gets total duration strictly grouped by chat name (all time).
     */
    fun getTopContactsAllTime(limit: Int): List<TopContact> {
        val results = mutableListOf<TopContact>()
        val query = """
            SELECT chat_name, SUM(duration_seconds) as total
            FROM usage_events_v2
            WHERE chat_name IS NOT NULL
              AND chat_name != 'Legacy Session'
              AND chat_name != 'WhatsApp'
            GROUP BY chat_name
            ORDER BY total DESC
            LIMIT ?
        """.trimIndent()
        
        val conn = Database.getConnection()
        conn.prepareStatement(query).use { pstmt ->
            pstmt.setInt(1, limit)
            val rs = pstmt.executeQuery()
            while (rs.next()) {
                results.add(TopContact(rs.getString("chat_name"), rs.getLong("total")))
            }
        }
        return results
    }

    // ── Private helpers ──────────────────────────────────────────────

    private fun startOfTodayMs(): Long {
        return LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
    
    private fun getStartOfDayFor(timestampMs: Long): Long {
        return java.time.Instant.ofEpochMilli(timestampMs)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    private fun queryEvents(sql: String, vararg params: Long): List<UsageEvent> {
        val events = mutableListOf<UsageEvent>()
        val conn = Database.getConnection()
        conn.prepareStatement(sql).use { pstmt ->
            params.forEachIndexed { index, value ->
                pstmt.setLong(index + 1, value)
            }
            val rs = pstmt.executeQuery()
            while (rs.next()) {
                events.add(
                    UsageEvent(
                        id = rs.getInt("id"),
                        dateMs = rs.getLong("date_ms"),
                        chatName = rs.getString("chat_name"),
                        durationSeconds = rs.getLong("duration_seconds")
                    )
                )
            }
        }
        return events
    }
}
