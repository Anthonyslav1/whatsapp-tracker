package com.whatsapptracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsapptracker.data.repository.UsageRepository
import com.whatsapptracker.data.model.YearlyReportData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class YearlyReportViewModel @Inject constructor(
    private val repository: UsageRepository
) : ViewModel() {

    private val _reportData = MutableStateFlow<YearlyReportData?>(null)
    val reportData: StateFlow<YearlyReportData?> = _reportData.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadReport()
    }

    private fun loadReport() {
        viewModelScope.launch {
            _isLoading.value = true
            _reportData.value = repository.getYearlyReport(LocalDate.now().year)
            _isLoading.value = false
        }
    }
}
