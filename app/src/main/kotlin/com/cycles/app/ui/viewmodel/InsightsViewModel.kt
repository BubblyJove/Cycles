package com.cycles.app.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cycles.app.CyclesApplication
import com.cycles.app.data.entity.Cycle
import com.cycles.app.data.entity.Prediction
import com.cycles.app.domain.CycleStats
import com.cycles.app.domain.DataExporter
import com.cycles.app.domain.PredictionEngine
import com.cycles.app.domain.PredictionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InsightsUiState(
    val stats: CycleStats? = null,
    val prediction: PredictionResult? = null,
    val cycles: List<Cycle> = emptyList(),
    val isLoading: Boolean = true,
    val shareIntent: Intent? = null,
)

class InsightsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as CyclesApplication).database
    private val cycleDao = db.cycleDao()
    private val dailyLogDao = db.dailyLogDao()
    private val predictionDao = db.predictionDao()
    private val predictionEngine = PredictionEngine()
    private val dataExporter = DataExporter(application)

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val completedCycles = cycleDao.getCompletedCycles()
            val allCycles = cycleDao.getAllCyclesOnce()
            val stats = predictionEngine.computeStats(completedCycles)

            val lastStart = allCycles.firstOrNull()?.startDate
            val prediction = if (lastStart != null) {
                predictionEngine.predict(completedCycles, lastStart)
            } else null

            if (prediction != null) {
                val entity = Prediction(
                    predictedStart = prediction.windowStart,
                    predictedEnd = prediction.windowEnd,
                    confidence = prediction.confidence,
                    rationale = prediction.rationale,
                )
                predictionDao.upsert(entity)
            }

            _uiState.value = InsightsUiState(
                stats = stats,
                prediction = prediction,
                cycles = allCycles,
                isLoading = false,
            )
        }
    }

    fun exportCsv() {
        viewModelScope.launch {
            val logs = dailyLogDao.getAllLogsOnce()
            val csv = dataExporter.generateCsv(logs)
            val intent = dataExporter.shareCsv(csv)
            _uiState.value = _uiState.value.copy(shareIntent = intent)
        }
    }

    fun exportPdf() {
        viewModelScope.launch {
            val logs = dailyLogDao.getAllLogsOnce()
            val stats = _uiState.value.stats
            val file = dataExporter.generatePdf(logs, stats)
            val intent = dataExporter.sharePdf(file)
            _uiState.value = _uiState.value.copy(shareIntent = intent)
        }
    }

    fun clearShareIntent() {
        _uiState.value = _uiState.value.copy(shareIntent = null)
    }
}
