package com.whatsapptracker.data.repository

import com.whatsapptracker.data.db.ChatSession
import com.whatsapptracker.data.db.ChatSessionDao
import com.whatsapptracker.data.db.ContactDuration
import com.whatsapptracker.data.db.MonthlyDuration
import com.whatsapptracker.data.db.DayOfWeekDuration
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Month
import java.time.DayOfWeek

class UsageRepositoryTest {

    private val dao: ChatSessionDao = mockk()
    private val repository = UsageRepository(dao)

    @Test
    fun testGetYearlyReport() = runBlocking {
        // Mock DAO responses
        coEvery { dao.getMonthlyDurations(any(), any()) } returns listOf(
            MonthlyDuration(month = 1, totalDuration = 6000L),
            MonthlyDuration(month = 2, totalDuration = 2000L)
        )
        coEvery { dao.getTopContactsYearly(any(), any(), any()) } returns listOf(
            ContactDuration("Alice", 3000L)
        )
        coEvery { dao.getUniqueContactCount(any(), any()) } returns 1
        coEvery { dao.getTotalSessionCount(any(), any()) } returns 3
        coEvery { dao.getLongestSession(any(), any()) } returns mockk(relaxed = true)
        coEvery { dao.getMostActiveDayOfWeek(any(), any()) } returns DayOfWeekDuration(1, 5000L) // 1 in SQLite = Monday

        val report = repository.getYearlyReport(2024)

        assertEquals(2024, report.year)
        assertEquals(8000L, report.totalDurationMs)
        assertEquals(1, report.uniqueContactCount)
        assertEquals(Month.JANUARY, report.monthlyDurations[0].first)
        assertEquals(Month.FEBRUARY, report.monthlyDurations[1].first)
        assertEquals(DayOfWeek.MONDAY, report.mostActiveDayOfWeek)
    }
}
