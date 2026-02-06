package com.cycles.app.domain

import com.cycles.app.data.entity.Cycle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PredictionEngineTest {

    private val engine = PredictionEngine()

    @Test
    fun `predict returns null with fewer than 2 cycles`() {
        val cycle = Cycle(
            id = 1,
            startDate = LocalDate.of(2025, 1, 1),
            cycleLength = 28,
        )
        val result = engine.predict(listOf(cycle), LocalDate.of(2025, 1, 29))
        assertNull(result)
    }

    @Test
    fun `predict returns null with zero cycles`() {
        val result = engine.predict(emptyList(), LocalDate.of(2025, 1, 1))
        assertNull(result)
    }

    @Test
    fun `predict returns window for regular cycles`() {
        val cycles = listOf(
            Cycle(id = 1, startDate = LocalDate.of(2025, 1, 1), cycleLength = 28),
            Cycle(id = 2, startDate = LocalDate.of(2025, 1, 29), cycleLength = 28),
            Cycle(id = 3, startDate = LocalDate.of(2025, 2, 26), cycleLength = 28),
        )
        val lastStart = LocalDate.of(2025, 3, 26)
        val result = engine.predict(cycles, lastStart)

        assertNotNull(result)
        // Mean=28, stdDev=0, margin=max(1, ceil(0))=1
        assertEquals(lastStart.plusDays(28).minusDays(1), result!!.windowStart)
        assertEquals(lastStart.plusDays(28).plusDays(1), result.windowEnd)
        // High confidence for regular cycles
        assertTrue(result.confidence > 0.2f)
    }

    @Test
    fun `predict widens window for irregular cycles`() {
        val cycles = listOf(
            Cycle(id = 1, startDate = LocalDate.of(2025, 1, 1), cycleLength = 24),
            Cycle(id = 2, startDate = LocalDate.of(2025, 1, 25), cycleLength = 35),
            Cycle(id = 3, startDate = LocalDate.of(2025, 3, 1), cycleLength = 28),
        )
        val lastStart = LocalDate.of(2025, 3, 29)
        val result = engine.predict(cycles, lastStart)

        assertNotNull(result)
        // Window should be wider than ±1 due to high stddev
        val windowSize = result!!.windowEnd.toEpochDay() - result.windowStart.toEpochDay()
        assertTrue("Window should be wider for irregular cycles", windowSize > 2)
        // Confidence should be lower
        assertTrue("Confidence should be lower for irregular cycles", result.confidence < 0.5f)
    }

    @Test
    fun `predict uses at most 6 recent cycles`() {
        val cycles = (1..10).map { i ->
            Cycle(
                id = i.toLong(),
                startDate = LocalDate.of(2025, 1, 1).plusDays((i * 28).toLong()),
                cycleLength = 28,
            )
        }
        val result = engine.predict(cycles, LocalDate.of(2025, 10, 1))
        assertNotNull(result)
    }

    @Test
    fun `predict filters out extreme cycle lengths`() {
        val cycles = listOf(
            Cycle(id = 1, startDate = LocalDate.of(2025, 1, 1), cycleLength = 10), // too short
            Cycle(id = 2, startDate = LocalDate.of(2025, 1, 11), cycleLength = 28),
            Cycle(id = 3, startDate = LocalDate.of(2025, 2, 8), cycleLength = 28),
        )
        val result = engine.predict(cycles, LocalDate.of(2025, 3, 8))
        assertNotNull(result)
    }

    @Test
    fun `confidence is clamped between 5 and 95 percent`() {
        val regularCycles = (1..12).map { i ->
            Cycle(
                id = i.toLong(),
                startDate = LocalDate.of(2025, 1, 1).plusDays((i * 28).toLong()),
                cycleLength = 28,
            )
        }
        val result = engine.predict(regularCycles, LocalDate.of(2025, 12, 1))
        assertNotNull(result)
        assertTrue(result!!.confidence <= 0.95f)
        assertTrue(result.confidence >= 0.05f)
    }

    @Test
    fun `computeStats returns null for empty list`() {
        val result = engine.computeStats(emptyList())
        assertNull(result)
    }

    @Test
    fun `computeStats returns correct averages`() {
        val cycles = listOf(
            Cycle(id = 1, startDate = LocalDate.of(2025, 1, 1), cycleLength = 28, periodLength = 5),
            Cycle(id = 2, startDate = LocalDate.of(2025, 1, 29), cycleLength = 30, periodLength = 4),
            Cycle(id = 3, startDate = LocalDate.of(2025, 2, 28), cycleLength = 26, periodLength = 6),
        )
        val result = engine.computeStats(cycles)

        assertNotNull(result)
        assertEquals(28f, result!!.averageCycleLength, 0.1f)
        assertEquals(26, result.shortestCycle)
        assertEquals(30, result.longestCycle)
        assertEquals(3, result.totalCycles)
        assertEquals(5f, result.averagePeriodLength!!, 0.1f)
    }

    @Test
    fun `computeStats handles missing period lengths`() {
        val cycles = listOf(
            Cycle(id = 1, startDate = LocalDate.of(2025, 1, 1), cycleLength = 28),
            Cycle(id = 2, startDate = LocalDate.of(2025, 1, 29), cycleLength = 30),
        )
        val result = engine.computeStats(cycles)

        assertNotNull(result)
        assertNull(result!!.averagePeriodLength)
    }

    @Test
    fun `rationale mentions cycle count`() {
        val cycles = listOf(
            Cycle(id = 1, startDate = LocalDate.of(2025, 1, 1), cycleLength = 28),
            Cycle(id = 2, startDate = LocalDate.of(2025, 1, 29), cycleLength = 30),
        )
        val result = engine.predict(cycles, LocalDate.of(2025, 2, 28))
        assertNotNull(result)
        assertTrue(result!!.rationale.contains("2 completed cycles"))
    }
}
