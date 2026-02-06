package com.cycles.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cycles.app.data.dao.CycleDao
import com.cycles.app.data.dao.DailyLogDao
import com.cycles.app.data.dao.PredictionDao
import com.cycles.app.data.dao.UserSettingsDao
import com.cycles.app.data.entity.Cycle
import com.cycles.app.data.entity.DailyLog
import com.cycles.app.data.entity.Prediction
import com.cycles.app.data.entity.UserSettings

@Database(
    entities = [
        DailyLog::class,
        Cycle::class,
        Prediction::class,
        UserSettings::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class CyclesDatabase : RoomDatabase() {
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun cycleDao(): CycleDao
    abstract fun predictionDao(): PredictionDao
    abstract fun userSettingsDao(): UserSettingsDao
}
