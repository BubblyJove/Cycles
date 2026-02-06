package com.cycles.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "daily_logs",
    indices = [Index(value = ["date"], unique = true)],
)
data class DailyLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "date")
    val date: LocalDate,

    @ColumnInfo(name = "bleeding_intensity")
    val bleedingIntensity: Int? = null, // 0=none, 1=light, 2=medium, 3=heavy, 4=very heavy

    @ColumnInfo(name = "pain_level")
    val painLevel: Int? = null, // 0=none, 1=mild, 2=moderate, 3=severe, 4=very severe

    @ColumnInfo(name = "mood")
    val mood: String? = null,

    @ColumnInfo(name = "discharge")
    val discharge: String? = null,

    @ColumnInfo(name = "medications")
    val medications: String? = null,

    @ColumnInfo(name = "sex_activity")
    val sexActivity: String? = null,

    @ColumnInfo(name = "bbt_temp")
    val bbtTemp: Float? = null,

    @ColumnInfo(name = "opk_result")
    val opkResult: String? = null, // "positive", "negative", "peak"

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
)
