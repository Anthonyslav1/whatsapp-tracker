package com.whatsapptracker.pc.db

import java.sql.Connection
import java.sql.DriverManager

object Database {
    private const val DB_FILE = "whatsapp_tracker.db"
    
    fun getConnection(): Connection {
        val url = "jdbc:sqlite:$DB_FILE"
        return DriverManager.getConnection(url)
    }

    fun initialize() {
        getConnection().use { conn ->
            val statement = conn.createStatement()
            statement.execute("""
                CREATE TABLE IF NOT EXISTS usage_events (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    timestamp_start INTEGER NOT NULL,
                    timestamp_end INTEGER NOT NULL,
                    duration_seconds INTEGER NOT NULL
                )
            """.trimIndent())
        }
    }
}
