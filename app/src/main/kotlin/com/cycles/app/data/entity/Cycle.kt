package com.cycles.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "cycles")
data class Cycle(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "start_date")
    val startDate: LocalDate,

    @ColumnInfo(name = "end_date")
    val endDate: LocalDate? = null,

    @ColumnInfo(name = "cycle_length")
    val cycleLength: Int? = null,

    @ColumnInfo(name = "period_length")
    val periodLength: Int? = null,
)
