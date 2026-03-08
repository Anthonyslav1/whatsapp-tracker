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
        var id = -1
        Database.getConnection().use { conn ->
            conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS).use { pstmt ->
                pstmt.setLong(1, event.timestampStart)
                pstmt.setLong(2, event.timestampEnd)
                pstmt.setLong(3, event.durationSeconds)
                pstmt.executeUpdate()

                val rs = pstmt.generatedKeys
                if (rs.next()) {
                    id = rs.getInt(1)
                }
            }
        }
        return id
    }

    fun getAllEvents(): List<UsageEvent> {
        val events = mutableListOf<UsageEvent>()
        val query = "SELECT * FROM usage_events ORDER BY timestamp_start DESC"
        Database.getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery(query)
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
        }
        return events
    }

    /**
     * Get today's sessions only.
     */
    fun getTodaySessions(): List<UsageEvent> {
        val startOfDay = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val events = mutableListOf<UsageEvent>()
        val query = "SELECT * FROM usage_events WHERE timestamp_start >= ? ORDER BY timestamp_start DESC"
        Database.getConnection().use { conn ->
            conn.prepareStatement(query).use { pstmt ->
                pstmt.setLong(1, startOfDay)
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
        }
        return events
    }

    /**
     * Get total seconds for today.
     */
    fun getTodayTotalSeconds(): Long {
        val startOfDay = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val query = "SELECT COALESCE(SUM(duration_seconds), 0) FROM usage_events WHERE timestamp_start >= ?"
        Database.getConnection().use { conn ->
            conn.prepareStatement(query).use { pstmt ->
                pstmt.setLong(1, startOfDay)
                val rs = pstmt.executeQuery()
                if (rs.next()) return rs.getLong(1)
            }
        }
        return 0L
    }

    /**
     * Get total seconds per day for the last 7 days.
     * Returns a map of LocalDate -> total seconds.
     */
    fun getWeeklyTotals(): Map<LocalDate, Long> {
        val today = LocalDate.now()
        val startDate = today.minusDays(6)
        val startMs = startDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val result = mutableMapOf<LocalDate, Long>()
        // Initialize all 7 days to 0
        for (i in 0..6) {
            result[startDate.plusDays(i.toLong())] = 0L
        }

        val query = "SELECT timestamp_start, duration_seconds FROM usage_events WHERE timestamp_start >= ?"
        Database.getConnection().use { conn ->
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
        }
        return result
    }

    /**
     * Get total session count for today.
     */
    fun getTodaySessionCount(): Int {
        val startOfDay = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val query = "SELECT COUNT(*) FROM usage_events WHERE timestamp_start >= ?"
        Database.getConnection().use { conn ->
            conn.prepareStatement(query).use { pstmt ->
                pstmt.setLong(1, startOfDay)
                val rs = pstmt.executeQuery()
                if (rs.next()) return rs.getInt(1)
            }
        }
        return 0
    }
}
