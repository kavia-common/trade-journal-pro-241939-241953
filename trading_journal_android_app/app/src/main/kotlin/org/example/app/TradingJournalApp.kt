package org.example.app

import android.app.Application
import org.example.app.data.local.AppDatabase

/**
 * Main Application class for Trading Journal Pro.
 * Initializes the database and provides application-level dependencies.
 */
// PUBLIC_INTERFACE
class TradingJournalApp : Application() {

    /** Lazily initialized Room database instance */
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: TradingJournalApp
            private set
    }
}
