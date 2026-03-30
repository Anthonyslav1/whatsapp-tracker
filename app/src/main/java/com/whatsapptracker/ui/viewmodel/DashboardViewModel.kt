package com.whatsapptracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsapptracker.data.db.ContactDuration
import com.whatsapptracker.data.repository.UsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: UsageRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    val todayTotalDuration: StateFlow<Long> = _selectedDate
        .flatMapLatest { date -> repository.getTodayTotalDuration(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val todayTopContacts: StateFlow<List<ContactDuration>> = _selectedDate
        .flatMapLatest { date -> repository.getTodayTopContacts(5, date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayTopEntertainers: StateFlow<List<ContactDuration>> = _selectedDate
        .flatMapLatest { date -> repository.getTopEntertainers(3, date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val smartInsights: StateFlow<String> = repository.getSmartInsights(
        LocalDate.now().minusDays(7).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
        LocalDate.now().plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Analyzing your communication metadata...")

    val weeklyTotals: StateFlow<Map<LocalDate, Long>> = repository.getWeeklyTotals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
}
