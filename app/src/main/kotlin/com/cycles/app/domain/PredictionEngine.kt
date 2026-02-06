package com.cycles.app.domain

import com.cycles.app.data.entity.Cycle
import java.time.LocalDate
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class PredictionResult(
    val windowStart: LocalDate,
    val windowEnd: LocalDate,
    val confidence: Float,
    val rationale: String,
)

class PredictionEngine {

    fun predict(completedCycles: List<Cycle>, lastCycleStart: LocalDate): PredictionResult? {
        if (completedCycles.size < 2) return null

        val lengths = completedCycles
            .take(6)
            .mapNotNull { it.cycleLength }
            .filter { it in 15..60 }

        if (lengths.size < 2) return null

        val mean = lengths.average()
        val variance = lengths.map { (it - mean) * (it - mean) }.average()
        val stdDev = sqrt(variance)

        val margin = max(1, ceil(stdDev).toInt())
        val centerDate = lastCycleStart.plusDays(mean.toLong())
        val windowStart = centerDate.minusDays(margin.toLong())
        val windowEnd = centerDate.plusDays(margin.toLong())

        val n = lengths.size
        val sampleFactor = min(n, 12).toFloat() / 12f
        val regularityFactor = 1f / (1f + stdDev.toFloat() / 3f)
        val confidence = (sampleFactor * regularityFactor).coerceIn(0.05f, 0.95f)

        val rationale = buildString {
            append("Based on ${lengths.size} completed cycle${if (lengths.size > 1) "s" else ""}. ")
            append("Average cycle length: ${String.format("%.1f", mean)} days. ")
            if (stdDev < 2.0) {
                append("Your cycles are quite regular (±${String.format("%.1f", stdDev)} days). ")
            } else if (stdDev < 5.0) {
                append("Your cycles have moderate variation (±${String.format("%.1f", stdDev)} days). ")
            } else {
                append("Your cycles vary significantly (±${String.format("%.1f", stdDev)} days). Predictions are less reliable. ")
            }
            append("Confidence: ${(confidence * 100).toInt()}%.")
        }

        return PredictionResult(
            windowStart = windowStart,
            windowEnd = windowEnd,
            confidence = confidence,
            rationale = rationale,
        )
    }

    fun computeStats(completedCycles: List<Cycle>): CycleStats? {
        val lengths = completedCycles.mapNotNull { it.cycleLength }.filter { it in 15..60 }
        if (lengths.isEmpty()) return null

        val periodLengths = completedCycles.mapNotNull { it.periodLength }.filter { it in 1..15 }

        return CycleStats(
            averageCycleLength = lengths.average().toFloat(),
            shortestCycle = lengths.min(),
            longestCycle = lengths.max(),
            totalCycles = lengths.size,
            averagePeriodLength = if (periodLengths.isNotEmpty()) periodLengths.average().toFloat() else null,
        )
    }
}

data class CycleStats(
    val averageCycleLength: Float,
    val shortestCycle: Int,
    val longestCycle: Int,
    val totalCycles: Int,
    val averagePeriodLength: Float?,
)
