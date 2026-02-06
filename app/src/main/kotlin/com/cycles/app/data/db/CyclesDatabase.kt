package com.cycles.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class CyclesDatabase : RoomDatabase() {
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun cycleDao(): CycleDao
    abstract fun predictionDao(): PredictionDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_daily_logs_date` ON `daily_logs` (`date`)"
                )
            }
        }
    }
}
