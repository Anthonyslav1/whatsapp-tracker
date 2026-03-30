package com.whatsapptracker.data.repository

import com.whatsapptracker.data.db.ContactDuration
import com.whatsapptracker.data.model.YearlyReportData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface UsageRepository {

    fun getTodayTotalDuration(date: LocalDate = LocalDate.now()): Flow<Long>

    fun getTodayTopContacts(limit: Int = 5, date: LocalDate = LocalDate.now()): Flow<List<ContactDuration>>

    fun getTopContactsByRelationshipScore(limit: Int = 5, date: LocalDate = LocalDate.now()): Flow<List<ContactDuration>>

    fun getSmartInsights(startTime: Long, endTime: Long): Flow<String>

    fun getTopEntertainers(limit: Int = 5, date: LocalDate = LocalDate.now()): Flow<List<ContactDuration>>

    fun getWeeklyTotals(): Flow<Map<LocalDate, Long>>

    suspend fun getYearlyReport(year: Int): YearlyReportData
}
