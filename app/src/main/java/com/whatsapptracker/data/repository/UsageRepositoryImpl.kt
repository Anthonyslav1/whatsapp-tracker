package com.whatsapptracker.data.repository

import com.whatsapptracker.data.db.ChatSessionDao
import com.whatsapptracker.data.db.ContactDuration
import com.whatsapptracker.data.model.YearlyReportData
import com.whatsapptracker.utils.TimeUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import javax.inject.Inject

class UsageRepositoryImpl @Inject constructor(
    private val dao: ChatSessionDao
) : UsageRepository {

    override fun getTodayTotalDuration(): Flow<Long> {
        val (start, end) = TimeUtils.getDayRangeMilli(LocalDate.now())
        return dao.getTotalDurationInRange(start, end)
    }

    override fun getTodayTopContacts(limit: Int): Flow<List<ContactDuration>> {
        val startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return dao.getTopContacts(startOfDay, endOfDay, limit)
    }

    override fun getTopContactsByRelationshipScore(limit: Int): Flow<List<ContactDuration>> {
        val startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return dao.getTopContactsByRelationshipScore(startOfDay, endOfDay, limit)
    }

    override fun getSmartInsights(startTime: Long, endTime: Long): Flow<String> {
        return dao.getTopContacts(startTime, endTime, 1).map { top -> 
            if (top.isNotEmpty()) {
                val best = top.first()
                val mins = best.totalDuration / 60000
                "Your usage recently is driven largely by ${best.contactName} ($mins mins). Your metadata shows you're a 'burst' communicator."
            } else {
                "Your communication signals are evenly distributed. You are a 'balanced' chat user."
            }
        }
    }

    override fun getTopEntertainers(limit: Int): Flow<List<ContactDuration>> {
        val startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return dao.getTopEntertainers(startOfDay, endOfDay, limit)
    }

    override fun getWeeklyTotals(): Flow<Map<LocalDate, Long>> {
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

    override suspend fun getYearlyReport(year: Int): YearlyReportData = coroutineScope {
        val startOfYear = TimeUtils.getStartOfDayEpochMilli(LocalDate.of(year, 1, 1))
        val endOfYear = TimeUtils.getStartOfDayEpochMilli(LocalDate.of(year + 1, 1, 1))

        // Parallelize Database Aggregations
        val monthlyListDeferred = async { dao.getMonthlyDurations(startOfYear, endOfYear) }
        val topContactsDeferred = async { dao.getTopContactsYearly(startOfYear, endOfYear, 100) }
        val topRelationshipContactsDeferred = async { dao.getTopContactsByRelationshipScoreYearly(startOfYear, endOfYear, 100) }
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
            topRelationshipContacts = topRelationshipContactsDeferred.await(),
            monthlyDurations = monthlyDurations,
            longestSession = longestSessionDeferred.await(),
            uniqueContactCount = uniqueContactCountDeferred.await(),
            totalSessionCount = totalSessionCountDeferred.await(),
            mostActiveDayOfWeek = mostActiveDayOfWeek,
            topEntertainer = topEntertainerDeferred.await()
        )
    }
}
