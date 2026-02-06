package com.cycles.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.cycles.app.data.entity.Cycle
import kotlinx.coroutines.flow.Flow

@Dao
interface CycleDao {

    @Query("SELECT * FROM cycles ORDER BY start_date DESC")
    fun getAllCycles(): Flow<List<Cycle>>

    @Query("SELECT * FROM cycles ORDER BY start_date DESC LIMIT 1")
    fun getLatestCycle(): Flow<Cycle?>

    @Query("SELECT * FROM cycles WHERE id = :id")
    fun getCycleById(id: Long): Flow<Cycle?>

    @Upsert
    suspend fun upsert(cycle: Cycle)

    @Delete
    suspend fun delete(cycle: Cycle)

    @Query("DELETE FROM cycles")
    suspend fun deleteAll()
}
