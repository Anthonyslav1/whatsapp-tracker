package com.whatsapptracker.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object TimeUtils {
    fun getLocalDateFromEpochMilli(epochMilli: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
        return Instant.ofEpochMilli(epochMilli).atZone(zoneId).toLocalDate()
    }
    
    fun getStartOfDayEpochMilli(date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Long {
        return date.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    fun getDayRangeMilli(date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Pair<Long, Long> {
        val start = getStartOfDayEpochMilli(date, zoneId)
        val end = getStartOfDayEpochMilli(date.plusDays(1), zoneId)
        return start to end
    }
}
