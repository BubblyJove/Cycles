package com.cycles.app.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.cycles.app.data.entity.Prediction
import kotlinx.coroutines.flow.Flow

@Dao
interface PredictionDao {

    @Query("SELECT * FROM predictions ORDER BY predicted_start DESC")
    fun getAllPredictions(): Flow<List<Prediction>>

    @Query("SELECT * FROM predictions ORDER BY created_at DESC LIMIT 1")
    fun getLatestPrediction(): Flow<Prediction?>

    @Query("SELECT * FROM predictions ORDER BY created_at DESC LIMIT 1")
    suspend fun getLatestPredictionOnce(): Prediction?

    @Upsert
    suspend fun upsert(prediction: Prediction)

    @Query("DELETE FROM predictions")
    suspend fun deleteAll()
}
