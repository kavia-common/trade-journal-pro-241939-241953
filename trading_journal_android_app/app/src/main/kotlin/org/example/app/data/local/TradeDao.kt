package org.example.app.data.local

import android.content.ContentValues
import android.database.Cursor
import org.example.app.data.model.Trade

/**
 * Data Access Object for Trade entities.
 * Provides CRUD operations on the trades table using raw SQLite.
 */
// PUBLIC_INTERFACE
class TradeDao(private val database: AppDatabase) {

    /**
     * Insert a new trade into the database.
     * @return the row ID of the newly inserted trade
     */
    // PUBLIC_INTERFACE
    fun insertTrade(trade: Trade): Long {
        val db = database.writableDatabase
        val values = tradeToContentValues(trade)
        return db.insert(AppDatabase.TABLE_TRADES, null, values)
    }

    /**
     * Update an existing trade.
     * @return number of rows updated
     */
    // PUBLIC_INTERFACE
    fun updateTrade(trade: Trade): Int {
        val db = database.writableDatabase
        val values = tradeToContentValues(trade)
        return db.update(AppDatabase.TABLE_TRADES, values, "id = ?", arrayOf(trade.id.toString()))
    }

    /**
     * Delete a trade by its ID.
     * @return number of rows deleted
     */
    // PUBLIC_INTERFACE
    fun deleteTrade(tradeId: Long): Int {
        val db = database.writableDatabase
        return db.delete(AppDatabase.TABLE_TRADES, "id = ?", arrayOf(tradeId.toString()))
    }

    /**
     * Get a trade by its ID.
     * @return the Trade or null if not found
     */
    // PUBLIC_INTERFACE
    fun getTradeById(tradeId: Long): Trade? {
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_TRADES,
            null,
            "id = ?",
            arrayOf(tradeId.toString()),
            null, null, null
        )
        return cursor.use {
            if (it.moveToFirst()) cursorToTrade(it) else null
        }
    }

    /**
     * Get all trades ordered by entry date descending.
     * @return list of all trades
     */
    // PUBLIC_INTERFACE
    fun getAllTrades(): List<Trade> {
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_TRADES,
            null, null, null, null, null,
            "entry_date DESC, created_at DESC"
        )
        return cursorToTradeList(cursor)
    }

    /**
     * Get trades filtered by status.
     */
    // PUBLIC_INTERFACE
    fun getTradesByStatus(status: String): List<Trade> {
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_TRADES,
            null,
            "status = ?",
            arrayOf(status),
            null, null,
            "entry_date DESC"
        )
        return cursorToTradeList(cursor)
    }

    /**
     * Get trades for a specific date.
     */
    // PUBLIC_INTERFACE
    fun getTradesByDate(date: String): List<Trade> {
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_TRADES,
            null,
            "entry_date = ?",
            arrayOf(date),
            null, null,
            "created_at DESC"
        )
        return cursorToTradeList(cursor)
    }

    /**
     * Search trades by symbol.
     */
    // PUBLIC_INTERFACE
    fun searchTradesBySymbol(query: String): List<Trade> {
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_TRADES,
            null,
            "symbol LIKE ?",
            arrayOf("%$query%"),
            null, null,
            "entry_date DESC"
        )
        return cursorToTradeList(cursor)
    }

    /**
     * Delete all trades from the database.
     * @return number of rows deleted
     */
    // PUBLIC_INTERFACE
    fun deleteAllTrades(): Int {
        val db = database.writableDatabase
        return db.delete(AppDatabase.TABLE_TRADES, null, null)
    }

    /**
     * Get count of trades.
     */
    // PUBLIC_INTERFACE
    fun getTradeCount(): Int {
        val db = database.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${AppDatabase.TABLE_TRADES}", null)
        return cursor.use {
            if (it.moveToFirst()) it.getInt(0) else 0
        }
    }

    /**
     * Get distinct trade dates for calendar view.
     */
    // PUBLIC_INTERFACE
    fun getDistinctTradeDates(): List<String> {
        val db = database.readableDatabase
        val cursor = db.rawQuery(
            "SELECT DISTINCT entry_date FROM ${AppDatabase.TABLE_TRADES} WHERE entry_date != '' ORDER BY entry_date DESC",
            null
        )
        val dates = mutableListOf<String>()
        cursor.use {
            while (it.moveToNext()) {
                dates.add(it.getString(0))
            }
        }
        return dates
    }

    private fun tradeToContentValues(trade: Trade): ContentValues {
        return ContentValues().apply {
            put("symbol", trade.symbol)
            put("trade_type", trade.tradeType)
            put("asset_class", trade.assetClass)
            put("entry_price", trade.entryPrice)
            put("exit_price", trade.exitPrice)
            put("quantity", trade.quantity)
            put("entry_date", trade.entryDate)
            put("exit_date", trade.exitDate)
            put("stop_loss", trade.stopLoss)
            put("take_profit", trade.takeProfit)
            put("fees", trade.fees)
            put("notes", trade.notes)
            put("tags", trade.tags)
            put("strategy", trade.strategy)
            put("status", trade.status)
            put("emotion", trade.emotion)
            put("screenshot_path", trade.screenshotPath)
            put("created_at", trade.createdAt)
            put("updated_at", trade.updatedAt)
        }
    }

    private fun cursorToTrade(cursor: Cursor): Trade {
        return Trade(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol")),
            tradeType = cursor.getString(cursor.getColumnIndexOrThrow("trade_type")),
            assetClass = cursor.getString(cursor.getColumnIndexOrThrow("asset_class")),
            entryPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("entry_price")),
            exitPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("exit_price")),
            quantity = cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
            entryDate = cursor.getString(cursor.getColumnIndexOrThrow("entry_date")),
            exitDate = cursor.getString(cursor.getColumnIndexOrThrow("exit_date")),
            stopLoss = cursor.getDouble(cursor.getColumnIndexOrThrow("stop_loss")),
            takeProfit = cursor.getDouble(cursor.getColumnIndexOrThrow("take_profit")),
            fees = cursor.getDouble(cursor.getColumnIndexOrThrow("fees")),
            notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")),
            tags = cursor.getString(cursor.getColumnIndexOrThrow("tags")),
            strategy = cursor.getString(cursor.getColumnIndexOrThrow("strategy")),
            status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
            emotion = cursor.getString(cursor.getColumnIndexOrThrow("emotion")),
            screenshotPath = cursor.getString(cursor.getColumnIndexOrThrow("screenshot_path")),
            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at")),
            updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"))
        )
    }

    private fun cursorToTradeList(cursor: Cursor): List<Trade> {
        val trades = mutableListOf<Trade>()
        cursor.use {
            while (it.moveToNext()) {
                trades.add(cursorToTrade(it))
            }
        }
        return trades
    }
}
