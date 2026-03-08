package com.whatsapptracker.pc.db

import java.sql.Statement
import java.time.LocalDate
import java.time.ZoneId

data class UsageEvent(
    val id: Int = 0,
    val timestampStart: Long,
    val timestampEnd: Long,
    val durationSeconds: Long
)

class UsageEventRepository {

    fun insertEvent(event: UsageEvent): Int {
        val query = "INSERT INTO usage_events(timestamp_start, timestamp_end, duration_seconds) VALUES (?, ?, ?)"
        val conn = Database.getConnection()
        conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS).use { pstmt ->
            pstmt.setLong(1, event.timestampStart)
            pstmt.setLong(2, event.timestampEnd)
            pstmt.setLong(3, event.durationSeconds)
            pstmt.executeUpdate()

            val rs = pstmt.generatedKeys
            if (rs.next()) return rs.getInt(1)
        }
        return -1
    }

    fun getAllEvents(): List<UsageEvent> {
        return queryEvents("SELECT * FROM usage_events ORDER BY timestamp_start DESC")
    }

    fun getTodaySessions(): List<UsageEvent> {
        val startOfDay = startOfTodayMs()
        return queryEvents(
            "SELECT * FROM usage_events WHERE timestamp_start >= ? ORDER BY timestamp_start DESC",
            startOfDay
        )
    }

    fun getTodayTotalSeconds(): Long {
        val startOfDay = startOfTodayMs()
        val query = "SELECT COALESCE(SUM(duration_seconds), 0) FROM usage_events WHERE timestamp_start >= ?"
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

        val query = "SELECT timestamp_start, duration_seconds FROM usage_events WHERE timestamp_start >= ?"
        val conn = Database.getConnection()
        conn.prepareStatement(query).use { pstmt ->
            pstmt.setLong(1, startMs)
            val rs = pstmt.executeQuery()
            while (rs.next()) {
                val ts = rs.getLong("timestamp_start")
                val dur = rs.getLong("duration_seconds")
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
        val query = "SELECT COUNT(*) FROM usage_events WHERE timestamp_start >= ?"
        val conn = Database.getConnection()
        conn.prepareStatement(query).use { pstmt ->
            pstmt.setLong(1, startOfDay)
            val rs = pstmt.executeQuery()
            if (rs.next()) return rs.getInt(1)
        }
        return 0
    }

    // ── Private helpers ──────────────────────────────────────────────

    private fun startOfTodayMs(): Long {
        return LocalDate.now()
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
                        timestampStart = rs.getLong("timestamp_start"),
                        timestampEnd = rs.getLong("timestamp_end"),
                        durationSeconds = rs.getLong("duration_seconds")
                    )
                )
            }
        }
        return events
    }
}
