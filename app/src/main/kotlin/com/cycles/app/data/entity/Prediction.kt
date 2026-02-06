package com.cycles.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "predictions")
data class Prediction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "predicted_start")
    val predictedStart: LocalDate,

    @ColumnInfo(name = "predicted_end")
    val predictedEnd: LocalDate,

    @ColumnInfo(name = "confidence")
    val confidence: Float, // 0.0 to 1.0

    @ColumnInfo(name = "rationale")
    val rationale: String? = null,

    @ColumnInfo(name = "cycle_id")
    val cycleId: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)
