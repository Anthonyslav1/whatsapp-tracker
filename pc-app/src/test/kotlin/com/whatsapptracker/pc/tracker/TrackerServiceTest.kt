package com.whatsapptracker.pc.tracker

import com.whatsapptracker.pc.db.Database
import com.whatsapptracker.pc.db.UsageEventRepository
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TrackerServiceTest {

    private lateinit var repository: UsageEventRepository
    private lateinit var service: TrackerService

    @Before
    fun setup() {
        File("whatsapp_tracker.db").delete()
        Database.initialize()
        repository = UsageEventRepository()
        service = TrackerService(repository)
    }

    @After
    fun teardown() {
        Database.close()
        File("whatsapp_tracker.db").delete()
    }

    // ── Initial state ──

    @Test
    fun `initial state is not tracking`() {
        assertFalse(service.isCurrentlyTracking)
        assertEquals(0L, service.currentSessionStartMs)
        assertEquals(null, service.currentChatName)
    }

    @Test
    fun `getLiveSessionSeconds returns 0 when not tracking`() {
        assertEquals(0L, service.getLiveSessionSeconds())
    }

    // ── stop() graceful shutdown ──

    @Test
    fun `stop flushes active session state`() {
        service.stop()
        assertFalse(service.isCurrentlyTracking)
        assertEquals(0L, service.currentSessionStartMs)
        assertEquals(null, service.currentChatName)
    }

    // ── Repository integration ──

    @Test
    fun `repository upserts handle direct inserts properly`() {
        val now = System.currentTimeMillis()
        repository.upsertEvent(now, "TestChat", 60)
        
        assertEquals(1, repository.getTodaySessionCount())
        assertEquals(60L, repository.getTodayTotalSeconds())

        // Another insert for the same chat adds to total
        repository.upsertEvent(now, "TestChat", 20)
        assertEquals(1, repository.getTodaySessionCount(), "Should still be 1 unified session/row for TestChat today")
        assertEquals(80L, repository.getTodayTotalSeconds())
    }
}
