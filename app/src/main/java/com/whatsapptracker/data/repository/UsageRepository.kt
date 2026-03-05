package com.whatsapptracker.data.repository

import com.whatsapptracker.data.db.ChatSession
import com.whatsapptracker.data.db.ChatSessionDao
import com.whatsapptracker.data.db.ContactDuration
import com.whatsapptracker.utils.TimeUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.*

data class YearlyReportData(
    val year: Int,
    val totalDurationMs: Long,
    val topContacts: List<ContactDuration>,
    val monthlyDurations: List<Pair<Month, Long>>,
    val longestSession: ChatSession?,
    val uniqueContactCount: Int,
    val totalSessionCount: Int,
    val mostActiveDayOfWeek: DayOfWeek?,
    val topEntertainer: ContactDuration?
)

class UsageRepository(private val dao: ChatSessionDao) {

    fun getTodayTotalDuration(): Flow<Long> {
        val (start, end) = TimeUtils.getDayRangeMilli(LocalDate.now())
        return dao.getTotalDurationInRange(start, end)
    }

    fun getTodayTopContacts(limit: Int): Flow<List<ContactDuration>> {
        val startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return dao.getTopContacts(startOfDay, endOfDay, limit)
    }

    fun getTopEntertainers(limit: Int): Flow<List<ContactDuration>> {
        val startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return dao.getTopEntertainers(startOfDay, endOfDay, limit)
    }

    fun getWeeklyTotals(): Flow<Map<LocalDate, Long>> {
        val today = LocalDate.now()
        val startOfWeek = today.minusDays(6)
        val start = TimeUtils.getStartOfDayEpochMilli(startOfWeek)
        val end = TimeUtils.getStartOfDayEpochMilli(today.plusDays(1))

        return dao.getSessionsInRange(start, end).map { sessions ->
            sessions.groupBy { session ->
                TimeUtils.getLocalDateFromEpochMilli(session.startTime)
            }.mapValues { (_, daySessions) ->
                daySessions.sumOf { it.durationMs }
            }
        }
    }

    suspend fun getYearlyReport(year: Int): YearlyReportData {
        val startOfYear = TimeUtils.getStartOfDayEpochMilli(LocalDate.of(year, 1, 1))
        val endOfYear = TimeUtils.getStartOfDayEpochMilli(LocalDate.of(year + 1, 1, 1))

        // Offload heavy aggregation to SQLite instead of in-memory groupBy
        val monthlyList = dao.getMonthlyDurations(startOfYear, endOfYear)
        val totalDuration = monthlyList.sumOf { it.totalDuration }

        val monthlyDurations = monthlyList.map { 
            Month.of(it.month) to it.totalDuration 
        }

        val topContacts = dao.getTopContactsYearly(startOfYear, endOfYear, 100)
        val uniqueContactCount = dao.getUniqueContactCount(startOfYear, endOfYear)
        val totalSessionCount = dao.getTotalSessionCount(startOfYear, endOfYear)
        val longestSession = dao.getLongestSession(startOfYear, endOfYear)

        val dowResult = dao.getMostActiveDayOfWeek(startOfYear, endOfYear)
        val mostActiveDayOfWeek = dowResult?.let { 
            // SQLite %w returns 0=Sunday, 1=Monday... 6=Saturday
            // java.time.DayOfWeek uses 1=Monday... 7=Sunday
            val mappedDay = if (it.dayOfWeek == 0) 7 else it.dayOfWeek
            DayOfWeek.of(mappedDay)
        }

        val topEntertainer = dao.getTopEntertainerYearly(startOfYear, endOfYear)

        return YearlyReportData(
            year = year,
            totalDurationMs = totalDuration,
            topContacts = topContacts,
            monthlyDurations = monthlyDurations,
            longestSession = longestSession,
            uniqueContactCount = uniqueContactCount,
            totalSessionCount = totalSessionCount,
            mostActiveDayOfWeek = mostActiveDayOfWeek,
            topEntertainer = topEntertainer
        )
    }
}
