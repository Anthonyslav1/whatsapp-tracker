package com.whatsapptracker.data.model

import com.whatsapptracker.data.db.ContactDuration
import com.whatsapptracker.data.db.ChatSession
import java.time.DayOfWeek
import java.time.Month

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
