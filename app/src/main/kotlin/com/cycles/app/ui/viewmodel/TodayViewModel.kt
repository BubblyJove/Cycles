package com.cycles.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cycles.app.CyclesApplication
import com.cycles.app.data.entity.DailyLog
import com.cycles.app.domain.CycleDetector
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TodayUiState(
    val date: LocalDate = LocalDate.now(),
    val log: DailyLog? = null,
    val isInPeriod: Boolean = false,
    val isLoading: Boolean = true,
)

class TodayViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as CyclesApplication).database
    private val dailyLogDao = db.dailyLogDao()
    private val cycleDao = db.cycleDao()
    private val cycleDetector = CycleDetector(cycleDao, dailyLogDao)

    private val _uiState = MutableStateFlow(TodayUiState())
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()

    private var saveJob: Job? = null

    init {
        viewModelScope.launch {
            dailyLogDao.getLogForDate(LocalDate.now()).collect { log ->
                val inPeriod = cycleDetector.isInPeriod()
                _uiState.value = _uiState.value.copy(
                    log = log,
                    isInPeriod = inPeriod,
                    isLoading = false,
                )
            }
        }
    }

    fun togglePeriod() {
        viewModelScope.launch {
            val today = _uiState.value.date
            if (_uiState.value.isInPeriod) {
                cycleDetector.endPeriod(today)
            } else {
                cycleDetector.startPeriod(today)
            }
        }
    }

    fun updateBleeding(level: Int) {
        updateLog { it.copy(bleedingIntensity = level) }
    }

    fun updatePain(level: Int) {
        updateLog { it.copy(painLevel = level) }
    }

    fun updateMood(mood: String?) {
        updateLog { it.copy(mood = mood) }
    }

    fun updateMedications(meds: String?) {
        updateLog { it.copy(medications = meds) }
    }

    fun updateNotes(notes: String) {
        val trimmed = notes.ifBlank { null }
        // Debounce notes saving
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(500)
            updateLogImmediate { it.copy(notes = trimmed) }
        }
    }

    private fun updateLog(transform: (DailyLog) -> DailyLog) {
        viewModelScope.launch {
            updateLogImmediate(transform)
        }
    }

    private suspend fun updateLogImmediate(transform: (DailyLog) -> DailyLog) {
        val today = _uiState.value.date
        val existing = dailyLogDao.getLogForDateOnce(today)
        val base = existing ?: DailyLog(date = today)
        val updated = transform(base).copy(updatedAt = System.currentTimeMillis())
        dailyLogDao.upsert(updated)
    }
}
