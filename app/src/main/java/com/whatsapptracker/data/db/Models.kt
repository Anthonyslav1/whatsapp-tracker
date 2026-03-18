package com.whatsapptracker.data.db

data class ContactDuration(
    val contactName: String,
    val totalDuration: Long
)

data class MonthlyDuration(
    val month: Int,
    val totalDuration: Long
)

data class DayOfWeekDuration(
    val dayOfWeek: Int,
    val totalDuration: Long
)
