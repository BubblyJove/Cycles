package com.cycles.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val key: String,

    @ColumnInfo(name = "value")
    val value: String,
)
