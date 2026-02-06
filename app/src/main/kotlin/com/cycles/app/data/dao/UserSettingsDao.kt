package com.cycles.app.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.cycles.app.data.entity.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {

    @Query("SELECT * FROM user_settings WHERE `key` = :key LIMIT 1")
    fun getSetting(key: String): Flow<UserSettings?>

    @Query("SELECT * FROM user_settings")
    fun getAllSettings(): Flow<List<UserSettings>>

    @Upsert
    suspend fun upsert(settings: UserSettings)

    @Query("DELETE FROM user_settings")
    suspend fun deleteAll()
}
