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

    // ── Insert + Retrieve ──

    @Test
    fun `insertEvent returns valid id`() {
        val event = makeEvent(durationSeconds = 120)
        val id = repository.insertEvent(event)
        assertTrue(id > 0, "Insert should return a positive ID")
    }

    @Test
    fun `getAllEvents returns inserted events in descending order`() {
        val now = System.currentTimeMillis()
        repository.insertEvent(makeEvent(now - 5000, now - 3000, 2))
        repository.insertEvent(makeEvent(now - 2000, now, 2))

        val events = repository.getAllEvents()
        assertEquals(2, events.size)
        assertTrue(events[0].timestampStart > events[1].timestampStart)
    }

    // ── Today aggregation ──

    @Test
    fun `getTodayTotalSeconds sums only today's events`() {
        val now = System.currentTimeMillis()
        // Today
        repository.insertEvent(makeEvent(now - 60_000, now, 60))
        repository.insertEvent(makeEvent(now - 120_000, now - 60_000, 60))
        // Yesterday (should be excluded)
        val yesterday = now - 86_400_000
        repository.insertEvent(makeEvent(yesterday, yesterday + 60_000, 60))

        val total = repository.getTodayTotalSeconds()
        assertEquals(120L, total)
    }

    @Test
    fun `getTodayTotalSeconds returns 0 when no events exist`() {
        assertEquals(0L, repository.getTodayTotalSeconds())
    }

    @Test
    fun `getTodaySessions returns only today's events`() {
        val now = System.currentTimeMillis()
        repository.insertEvent(makeEvent(now - 60_000, now, 60))
        val yesterday = now - 86_400_000
        repository.insertEvent(makeEvent(yesterday, yesterday + 60_000, 60))

        val sessions = repository.getTodaySessions()
        assertEquals(1, sessions.size)
    }

    @Test
    fun `getTodaySessionCount counts only today`() {
        val now = System.currentTimeMillis()
        repository.insertEvent(makeEvent(now - 60_000, now, 60))
        repository.insertEvent(makeEvent(now - 120_000, now - 60_000, 60))
        val yesterday = now - 86_400_000
        repository.insertEvent(makeEvent(yesterday, yesterday + 60_000, 60))

        assertEquals(2, repository.getTodaySessionCount())
    }

    // ── Weekly aggregation ──

    @Test
    fun `getWeeklyTotals returns 7 days with correct sums`() {
        val today = LocalDate.now()
        val todayMs = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        // Add 2 events today
        repository.insertEvent(makeEvent(todayMs + 1000, todayMs + 61_000, 60))
        repository.insertEvent(makeEvent(todayMs + 70_000, todayMs + 130_000, 60))

        // Add 1 event 3 days ago
        val threeDaysAgoMs = today.minusDays(3)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        repository.insertEvent(makeEvent(threeDaysAgoMs + 1000, threeDaysAgoMs + 301_000, 300))

        val weekly = repository.getWeeklyTotals()
        assertEquals(7, weekly.size, "Should always return 7 days")
        assertEquals(120L, weekly[today])
        assertEquals(300L, weekly[today.minusDays(3)])
        assertEquals(0L, weekly[today.minusDays(1)])
    }

    @Test
    fun `getWeeklyTotals excludes events older than 7 days`() {
        val eightDaysAgoMs = LocalDate.now().minusDays(8)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        repository.insertEvent(makeEvent(eightDaysAgoMs, eightDaysAgoMs + 60_000, 60))

        val weekly = repository.getWeeklyTotals()
        val totalSeconds = weekly.values.sum()
        assertEquals(0L, totalSeconds, "8-day-old events should be excluded")
    }

    // ── Edge cases ──

    @Test
    fun `inserting a 0-second event still works`() {
        val now = System.currentTimeMillis()
        val id = repository.insertEvent(makeEvent(now, now, 0))
        assertTrue(id > 0)
        // But it should be counted
        assertEquals(1, repository.getTodaySessionCount())
        assertEquals(0L, repository.getTodayTotalSeconds())
    }

    @Test
    fun `rapid successive inserts all persist`() {
        val now = System.currentTimeMillis()
        repeat(50) { i ->
            repository.insertEvent(makeEvent(now + i, now + i + 1000, 1))
        }
        assertEquals(50, repository.getTodaySessionCount())
        assertEquals(50L, repository.getTodayTotalSeconds())
    }

    // ── Helpers ──

    private fun makeEvent(
        start: Long = System.currentTimeMillis() - 60_000,
        end: Long = System.currentTimeMillis(),
        durationSeconds: Long = 60
    ) = UsageEvent(
        timestampStart = start,
        timestampEnd = end,
        durationSeconds = durationSeconds
    )
}
