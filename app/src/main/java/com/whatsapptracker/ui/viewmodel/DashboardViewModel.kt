package com.whatsapptracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsapptracker.data.db.ContactDuration
import com.whatsapptracker.data.repository.UsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: UsageRepository
) : ViewModel() {

    val todayTotalDuration: StateFlow<Long> = repository.getTodayTotalDuration()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val todayTopContacts: StateFlow<List<ContactDuration>> = repository.getTodayTopContacts(5)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayTopEntertainers: StateFlow<List<ContactDuration>> = repository.getTopEntertainers(3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklyTotals: StateFlow<Map<LocalDate, Long>> = repository.getWeeklyTotals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
}
