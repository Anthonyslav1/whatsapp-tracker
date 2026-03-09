package com.whatsapptracker.pc.db

import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UsageEventRepositoryTest {

    private lateinit var repository: UsageEventRepository

    @Before
    fun setup() {
        // Use a fresh in-memory style test DB (delete file before each test)
        File("whatsapp_tracker.db").delete()
        Database.initialize()
        repository = UsageEventRepository()
    }

    @After
    fun teardown() {
        Database.close()
        File("whatsapp_tracker.db").delete()
    }

    // ── Upsert Logic ──

    @Test
    fun `upsertEvent limits to 1 row per person per day`() {
        val todayMs = System.currentTimeMillis()
        
        repository.upsertEvent(todayMs, "Alice", 60)
        repository.upsertEvent(todayMs, "Alice", 120) // Should update the first one
        repository.upsertEvent(todayMs, "Bob", 30)    // New person

        val events = repository.getAllEvents()
        assertEquals(2, events.size, "Should be exactly 2 rows (Alice and Bob)")
        
        val aliceRow = events.find { it.chatName == "Alice" }
        assertEquals(180L, aliceRow?.durationSeconds, "Alice's time should be summed (180s)")
        
        val bobRow = events.find { it.chatName == "Bob" }
        assertEquals(30L, bobRow?.durationSeconds)
    }

    @Test
    fun `upsertEvent handles null chatName correctly`() {
        val todayMs = System.currentTimeMillis()
        repository.upsertEvent(todayMs, null, 10)
        repository.upsertEvent(todayMs, null, 15)

        val events = repository.getAllEvents()
        assertEquals(1, events.size)
        assertEquals(null, events[0].chatName)
        assertEquals(25L, events[0].durationSeconds)
    }

    // ── Aggregation ──

    @Test
    fun `getTopContactsAllTime aggregates correctly`() {
        val todayMs = System.currentTimeMillis()
        val yesterdayMs = todayMs - 86_400_000

        // Alice: 60s today + 40s yesterday = 100s
        repository.upsertEvent(todayMs, "Alice", 60)
        repository.upsertEvent(yesterdayMs, "Alice", 40)
        
        // Bob: 50s today
        repository.upsertEvent(todayMs, "Bob", 50)
        
        // Null/Menu should be ignored
        repository.upsertEvent(todayMs, null, 1000)

        val topContacts = repository.getTopContactsAllTime(10)
        
        assertEquals(2, topContacts.size)
        // Alice should be first
        assertEquals("Alice", topContacts[0].chatName)
        assertEquals(100L, topContacts[0].totalSeconds)
        // Bob second
        assertEquals("Bob", topContacts[1].chatName)
        assertEquals(50L, topContacts[1].totalSeconds)
    }

    @Test
    fun `getTodayTotalSeconds sums everything across chats`() {
        val todayMs = System.currentTimeMillis()
        repository.upsertEvent(todayMs, "Alice", 60)
        repository.upsertEvent(todayMs, "Bob", 120)
        repository.upsertEvent(todayMs, null, 20)
        
        // Yesterday (excluded)
        repository.upsertEvent(todayMs - 86_400_000, "Alice", 100)

        assertEquals(200L, repository.getTodayTotalSeconds())
    }

    // ── Weekly aggregation ──

    @Test
    fun `getWeeklyTotals returns 7 days with correct sums`() {
        val today = LocalDate.now()
        val todayMs = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        // Today
        repository.upsertEvent(todayMs, "Alice", 60)
        repository.upsertEvent(todayMs, "Bob", 60)

        // 3 days ago
        val threeDaysAgoMs = today.minusDays(3)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        repository.upsertEvent(threeDaysAgoMs, "Alice", 300)

        val weekly = repository.getWeeklyTotals()
        assertEquals(7, weekly.size, "Should always return 7 days")
        assertEquals(120L, weekly[today])
        assertEquals(300L, weekly[today.minusDays(3)])
        assertEquals(0L, weekly[today.minusDays(1)])
    }
}
