package com.cycles.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.cycles.app.data.entity.DailyLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailyLogDao {

    @Query("SELECT * FROM daily_logs WHERE date = :date LIMIT 1")
    fun getLogForDate(date: LocalDate): Flow<DailyLog?>

    @Query("SELECT * FROM daily_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<DailyLog>>

    @Query("SELECT * FROM daily_logs WHERE date BETWEEN :start AND :end ORDER BY date ASC")
    fun getLogsBetween(start: LocalDate, end: LocalDate): Flow<List<DailyLog>>

    @Upsert
    suspend fun upsert(log: DailyLog)

    @Delete
    suspend fun delete(log: DailyLog)

    @Query("DELETE FROM daily_logs")
    suspend fun deleteAll()
}
