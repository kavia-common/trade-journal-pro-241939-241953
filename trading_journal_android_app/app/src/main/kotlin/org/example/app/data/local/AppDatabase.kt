package org.example.app.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * SQLite database helper for Trading Journal.
 * Uses raw SQLite instead of Room annotation processing
 * since DCL build files don't support kapt/ksp annotation processors.
 */
// PUBLIC_INTERFACE
class AppDatabase private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TRADES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRADES")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "trading_journal.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_TRADES = "trades"

        private const val CREATE_TRADES_TABLE = """
            CREATE TABLE IF NOT EXISTS $TABLE_TRADES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                symbol TEXT NOT NULL DEFAULT '',
                trade_type TEXT NOT NULL DEFAULT 'BUY',
                asset_class TEXT NOT NULL DEFAULT 'Stock',
                entry_price REAL NOT NULL DEFAULT 0.0,
                exit_price REAL NOT NULL DEFAULT 0.0,
                quantity REAL NOT NULL DEFAULT 0.0,
                entry_date TEXT NOT NULL DEFAULT '',
                exit_date TEXT NOT NULL DEFAULT '',
                stop_loss REAL NOT NULL DEFAULT 0.0,
                take_profit REAL NOT NULL DEFAULT 0.0,
                fees REAL NOT NULL DEFAULT 0.0,
                notes TEXT NOT NULL DEFAULT '',
                tags TEXT NOT NULL DEFAULT '',
                strategy TEXT NOT NULL DEFAULT '',
                status TEXT NOT NULL DEFAULT 'OPEN',
                emotion TEXT NOT NULL DEFAULT 'Neutral',
                screenshot_path TEXT NOT NULL DEFAULT '',
                created_at INTEGER NOT NULL DEFAULT 0,
                updated_at INTEGER NOT NULL DEFAULT 0
            )
        """

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton instance of AppDatabase.
         */
        // PUBLIC_INTERFACE
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppDatabase(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
}
