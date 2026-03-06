package com.whatsapptracker.data.repository

import com.whatsapptracker.data.db.ChatSession
import com.whatsapptracker.data.db.ChatSessionDao
import com.whatsapptracker.data.db.ContactDuration
import com.whatsapptracker.utils.TimeUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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

    suspend fun getYearlyReport(year: Int): YearlyReportData = coroutineScope {
        val startOfYear = TimeUtils.getStartOfDayEpochMilli(LocalDate.of(year, 1, 1))
        val endOfYear = TimeUtils.getStartOfDayEpochMilli(LocalDate.of(year + 1, 1, 1))

        // Parallelize Database Aggregations
        val monthlyListDeferred = async { dao.getMonthlyDurations(startOfYear, endOfYear) }
        val topContactsDeferred = async { dao.getTopContactsYearly(startOfYear, endOfYear, 100) }
        val uniqueContactCountDeferred = async { dao.getUniqueContactCount(startOfYear, endOfYear) }
        val totalSessionCountDeferred = async { dao.getTotalSessionCount(startOfYear, endOfYear) }
        val longestSessionDeferred = async { dao.getLongestSession(startOfYear, endOfYear) }
        val dowResultDeferred = async { dao.getMostActiveDayOfWeek(startOfYear, endOfYear) }
        val topEntertainerDeferred = async { dao.getTopEntertainerYearly(startOfYear, endOfYear) }
        
        val monthlyList = monthlyListDeferred.await()
        val totalDuration = monthlyList.sumOf { it.totalDuration }

        val monthlyDurations = monthlyList.map { 
            Month.of(it.month) to it.totalDuration 
        }

        val dowResult = dowResultDeferred.await()
        val mostActiveDayOfWeek = dowResult?.let { 
            val mappedDay = if (it.dayOfWeek == 0) 7 else it.dayOfWeek
            DayOfWeek.of(mappedDay)
        }

        return@coroutineScope YearlyReportData(
            year = year,
            totalDurationMs = totalDuration,
            topContacts = topContactsDeferred.await(),
            monthlyDurations = monthlyDurations,
            longestSession = longestSessionDeferred.await(),
            uniqueContactCount = uniqueContactCountDeferred.await(),
            totalSessionCount = totalSessionCountDeferred.await(),
            mostActiveDayOfWeek = mostActiveDayOfWeek,
            topEntertainer = topEntertainerDeferred.await()
        )
    }
}
