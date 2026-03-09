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

        // Create the new v2 schema with daily upsert logic
        statement.execute("""
            CREATE TABLE IF NOT EXISTS usage_events_v2 (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date_ms INTEGER NOT NULL,
                chat_name TEXT,
                duration_seconds INTEGER NOT NULL,
                UNIQUE(date_ms, chat_name)
            )
        """.trimIndent())

        // Index for time-range queries (today, weekly)
        statement.execute("""
            CREATE INDEX IF NOT EXISTS idx_usage_v2_date 
            ON usage_events_v2(date_ms)
        """.trimIndent())

        // Migrate legacy v1 data if v2 is empty
        migrateLegacyData(conn)
    }

    private fun migrateLegacyData(conn: Connection) {
        val statement = conn.createStatement()
        
        // Check if v2 is empty
        val rs = statement.executeQuery("SELECT COUNT(*) FROM usage_events_v2")
        val v2Count = if (rs.next()) rs.getInt(1) else 0

        if (v2Count == 0) {
            try {
                // Check if v1 table actually exists
                statement.executeQuery("SELECT 1 FROM usage_events LIMIT 1")
                
                // If it exists, migrate it over. We take the timestamp_start, 
                // truncate to the start of that day (using SQLite date modifiers),
                // but for simplicity since JVM and SQLite timezones can differ, 
                // we'll just insert the exact ms and label the chat as 'Legacy Session'.
                statement.execute("""
                    INSERT INTO usage_events_v2 (date_ms, chat_name, duration_seconds)
                    SELECT timestamp_start, 'Legacy Session', duration_seconds
                    FROM usage_events
                """.trimIndent())
            } catch (e: Exception) {
                // Legacy table does not exist, nothing to migrate
            }
        }
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
