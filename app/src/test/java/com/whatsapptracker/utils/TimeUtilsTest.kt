package com.whatsapptracker.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class TimeUtilsTest {

    @Test
    fun testGetLocalDateFromEpochMilli() {
        val zone = ZoneId.of("UTC")
        val epoch = 1704067200000L // 2024-01-01T00:00:00Z
        val date = TimeUtils.getLocalDateFromEpochMilli(epoch, zone)
        assertEquals(LocalDate.of(2024, 1, 1), date)
    }

    @Test
    fun testGetStartOfDayEpochMilli() {
        val zone = ZoneId.of("UTC")
        val date = LocalDate.of(2024, 1, 1)
        val epoch = TimeUtils.getStartOfDayEpochMilli(date, zone)
        assertEquals(1704067200000L, epoch)
    }

    @Test
    fun testGetDayRangeMilli() {
        val zone = ZoneId.of("UTC")
        val date = LocalDate.of(2024, 1, 1)
        val (start, end) = TimeUtils.getDayRangeMilli(date, zone)
        assertEquals(1704067200000L, start)
        assertEquals(1704153600000L, end) // + 24 hours
    }
    
    @Test
    fun testLeapYear() {
        val zone = ZoneId.of("UTC")
        val date = LocalDate.of(2024, 2, 29)
        val (start, end) = TimeUtils.getDayRangeMilli(date, zone)
        assertEquals(TimeUtils.getLocalDateFromEpochMilli(start, zone), LocalDate.of(2024, 2, 29))
        assertEquals(TimeUtils.getLocalDateFromEpochMilli(end, zone), LocalDate.of(2024, 3, 1))
    }
}
