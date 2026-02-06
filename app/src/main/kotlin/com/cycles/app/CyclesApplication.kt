package com.cycles.app

import android.app.Application
import com.cycles.app.data.db.DatabaseProvider

class CyclesApplication : Application() {

    val database by lazy { DatabaseProvider.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
    }
}
