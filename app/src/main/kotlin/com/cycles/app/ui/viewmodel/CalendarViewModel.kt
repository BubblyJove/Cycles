package com.cycles.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cycles.app.CyclesApplication
import com.cycles.app.data.entity.Cycle
import com.cycles.app.data.entity.DailyLog
import com.cycles.app.data.entity.Prediction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val logs: Map<LocalDate, DailyLog> = emptyMap(),
    val cycles: List<Cycle> = emptyList(),
    val prediction: Prediction? = null,
    val selectedDate: LocalDate? = null,
    val selectedLog: DailyLog? = null,
    val isLoading: Boolean = true,
)

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as CyclesApplication).database
    private val dailyLogDao = db.dailyLogDao()
    private val cycleDao = db.cycleDao()
    private val predictionDao = db.predictionDao()

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadMonth(YearMonth.now())
    }

    fun navigateMonth(offset: Int) {
        val newMonth = _uiState.value.currentMonth.plusMonths(offset.toLong())
        loadMonth(newMonth)
    }

    fun selectDate(date: LocalDate) {
        val log = _uiState.value.logs[date]
        _uiState.value = _uiState.value.copy(selectedDate = date, selectedLog = log)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedDate = null, selectedLog = null)
    }

    private fun loadMonth(month: YearMonth) {
        _uiState.value = _uiState.value.copy(currentMonth = month, isLoading = true)
        val start = month.atDay(1)
        val end = month.atEndOfMonth()

        viewModelScope.launch {
            dailyLogDao.getLogsBetween(start, end).collect { logs ->
                val logMap = logs.associateBy { it.date }
                val cycles = cycleDao.getAllCyclesOnce()
                val prediction = predictionDao.getLatestPredictionOnce()
                _uiState.value = _uiState.value.copy(
                    logs = logMap,
                    cycles = cycles,
                    prediction = prediction,
                    isLoading = false,
                )
            }
        }
    }
}
