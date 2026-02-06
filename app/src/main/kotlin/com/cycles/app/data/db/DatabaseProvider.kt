package com.cycles.app.data.db

import android.content.Context
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.util.UUID

object DatabaseProvider {

    @Volatile
    private var INSTANCE: CyclesDatabase? = null

    fun getDatabase(context: Context): CyclesDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }
    }

    private fun buildDatabase(context: Context): CyclesDatabase {
        System.loadLibrary("sqlcipher")
        val passphrase = getOrCreatePassphrase(context)
        val factory = SupportOpenHelperFactory(passphrase)
        return Room.databaseBuilder(
            context.applicationContext,
            CyclesDatabase::class.java,
            "cycles_encrypted.db",
        )
            .openHelperFactory(factory)
            .build()
    }

    private fun getOrCreatePassphrase(context: Context): ByteArray {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val prefs = EncryptedSharedPreferences.create(
            context,
            "cycles_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

        val existing = prefs.getString("db_passphrase", null)
        if (existing != null) {
            return existing.toByteArray()
        }

        val newPassphrase = UUID.randomUUID().toString()
        prefs.edit().putString("db_passphrase", newPassphrase).apply()
        return newPassphrase.toByteArray()
    }
}
