package com.cycles.app.domain

import com.cycles.app.data.dao.CycleDao
import com.cycles.app.data.dao.DailyLogDao
import com.cycles.app.data.entity.Cycle
import com.cycles.app.data.entity.DailyLog
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CycleDetector(
    private val cycleDao: CycleDao,
    private val dailyLogDao: DailyLogDao,
) {
    suspend fun startPeriod(date: LocalDate) {
        val openCycle = cycleDao.getOpenCycle()
        if (openCycle != null) {
            // Auto-close the previous cycle
            val cycleLength = ChronoUnit.DAYS.between(openCycle.startDate, date).toInt()
            val periodLength = if (openCycle.endDate != null) {
                ChronoUnit.DAYS.between(openCycle.startDate, openCycle.endDate).toInt() + 1
            } else {
                null
            }
            cycleDao.upsert(
                openCycle.copy(
                    cycleLength = cycleLength,
                    periodLength = periodLength,
                )
            )
        }
        // Create new open cycle
        cycleDao.upsert(Cycle(startDate = date))

        // Ensure today's log has bleeding set
        val existingLog = dailyLogDao.getLogForDateOnce(date)
        if (existingLog != null) {
            if (existingLog.bleedingIntensity == null || existingLog.bleedingIntensity == 0) {
                dailyLogDao.upsert(existingLog.copy(bleedingIntensity = 1, updatedAt = System.currentTimeMillis()))
            }
        } else {
            dailyLogDao.upsert(DailyLog(date = date, bleedingIntensity = 1))
        }
    }

    suspend fun endPeriod(date: LocalDate) {
        val openCycle = cycleDao.getOpenCycle() ?: return
        val periodLength = ChronoUnit.DAYS.between(openCycle.startDate, date).toInt() + 1
        cycleDao.upsert(
            openCycle.copy(
                endDate = date,
                periodLength = periodLength,
            )
        )
    }

    suspend fun isInPeriod(): Boolean {
        val openCycle = cycleDao.getOpenCycle() ?: return false
        return openCycle.endDate == null
    }
}
