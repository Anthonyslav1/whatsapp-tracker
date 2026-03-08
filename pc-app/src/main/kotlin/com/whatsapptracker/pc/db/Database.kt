package com.whatsapptracker.pc.db

import java.sql.Connection
import java.sql.DriverManager

object Database {
    private const val DB_FILE = "whatsapp_tracker.db"

    @Volatile
    private var connection: Connection? = null

    /**
     * Returns a singleton connection. SQLite JDBC operates in SERIALIZED mode
     * by default, so a single connection is thread-safe.
     */
    fun getConnection(): Connection {
        val conn = connection
        if (conn != null && !conn.isClosed) return conn

        val newConn = DriverManager.getConnection("jdbc:sqlite:$DB_FILE")
        // Enable WAL mode for better concurrent read performance
        newConn.createStatement().execute("PRAGMA journal_mode=WAL")
        connection = newConn
        return newConn
    }

    fun initialize() {
        val conn = getConnection()
        val statement = conn.createStatement()
        statement.execute("""
            CREATE TABLE IF NOT EXISTS usage_events (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp_start INTEGER NOT NULL,
                timestamp_end INTEGER NOT NULL,
                duration_seconds INTEGER NOT NULL
            )
        """.trimIndent())

        // Index for time-range queries (today, weekly)
        statement.execute("""
            CREATE INDEX IF NOT EXISTS idx_usage_ts
            ON usage_events(timestamp_start)
        """.trimIndent())
    }

    /**
     * Close the singleton connection on app shutdown.
     */
    fun close() {
        try {
            connection?.close()
        } catch (_: Exception) {}
        connection = null
    }
}
