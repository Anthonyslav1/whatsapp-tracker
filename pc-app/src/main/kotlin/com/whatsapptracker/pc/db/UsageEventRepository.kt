package com.whatsapptracker.pc.db

import java.sql.Statement

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
}
