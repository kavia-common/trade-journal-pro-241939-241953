package org.example.app.data.repository

import org.example.app.data.local.TradeDao
import org.example.app.data.model.AnalyticsSummary
import org.example.app.data.model.Trade
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Repository that manages trade data from local database.
 * Acts as the single source of truth for trade data (offline-first).
 */
// PUBLIC_INTERFACE
class TradeRepository(private val tradeDao: TradeDao) {

    /**
     * Get all trades from local database.
     */
    // PUBLIC_INTERFACE
    fun getAllTrades(): List<Trade> = tradeDao.getAllTrades()

    /**
     * Get a single trade by ID.
     */
    // PUBLIC_INTERFACE
    fun getTradeById(id: Long): Trade? = tradeDao.getTradeById(id)

    /**
     * Insert a new trade.
     */
    // PUBLIC_INTERFACE
    fun insertTrade(trade: Trade): Long = tradeDao.insertTrade(trade)

    /**
     * Update an existing trade.
     */
    // PUBLIC_INTERFACE
    fun updateTrade(trade: Trade): Int = tradeDao.updateTrade(trade)

    /**
     * Delete a trade by ID.
     */
    // PUBLIC_INTERFACE
    fun deleteTrade(id: Long): Int = tradeDao.deleteTrade(id)

    /**
     * Delete all trades.
     */
    // PUBLIC_INTERFACE
    fun deleteAllTrades(): Int = tradeDao.deleteAllTrades()

    /**
     * Get trades filtered by status.
     */
    // PUBLIC_INTERFACE
    fun getTradesByStatus(status: String): List<Trade> = tradeDao.getTradesByStatus(status)

    /**
     * Get trades for a specific date.
     */
    // PUBLIC_INTERFACE
    fun getTradesByDate(date: String): List<Trade> = tradeDao.getTradesByDate(date)

    /**
     * Search trades by symbol.
     */
    // PUBLIC_INTERFACE
    fun searchTrades(query: String): List<Trade> = tradeDao.searchTradesBySymbol(query)

    /**
     * Get distinct trade dates for calendar.
     */
    // PUBLIC_INTERFACE
    fun getDistinctTradeDates(): List<String> = tradeDao.getDistinctTradeDates()

    /**
     * Compute analytics summary from all closed trades.
     */
    // PUBLIC_INTERFACE
    fun getAnalyticsSummary(): AnalyticsSummary {
        val allTrades = tradeDao.getAllTrades()
        val closedTrades = allTrades.filter { it.status == "CLOSED" }
        val openTrades = allTrades.filter { it.status == "OPEN" }

        if (closedTrades.isEmpty()) {
            return AnalyticsSummary(
                totalTrades = allTrades.size,
                openTrades = openTrades.size
            )
        }

        val winningTrades = closedTrades.filter { it.profitLoss > 0 }
        val losingTrades = closedTrades.filter { it.profitLoss < 0 }

        val totalPL = closedTrades.sumOf { it.profitLoss }
        val totalGains = winningTrades.sumOf { it.profitLoss }
        val totalLosses = kotlin.math.abs(losingTrades.sumOf { it.profitLoss })

        val winRate = if (closedTrades.isNotEmpty()) {
            (winningTrades.size.toDouble() / closedTrades.size) * 100.0
        } else 0.0

        val avgWin = if (winningTrades.isNotEmpty()) {
            winningTrades.sumOf { it.profitLoss } / winningTrades.size
        } else 0.0

        val avgLoss = if (losingTrades.isNotEmpty()) {
            losingTrades.sumOf { it.profitLoss } / losingTrades.size
        } else 0.0

        val profitFactor = if (totalLosses > 0) totalGains / totalLosses else 0.0

        val largestWin = winningTrades.maxOfOrNull { it.profitLoss } ?: 0.0
        val largestLoss = losingTrades.minOfOrNull { it.profitLoss } ?: 0.0

        val totalFees = closedTrades.sumOf { it.fees }

        // Calculate average holding days
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val holdingDays = closedTrades.mapNotNull { trade ->
            try {
                if (trade.entryDate.isNotEmpty() && trade.exitDate.isNotEmpty()) {
                    val entry = dateFormat.parse(trade.entryDate)
                    val exit = dateFormat.parse(trade.exitDate)
                    if (entry != null && exit != null) {
                        val diff = exit.time - entry.time
                        TimeUnit.MILLISECONDS.toDays(diff).toDouble()
                    } else null
                } else null
            } catch (e: Exception) {
                null
            }
        }
        val avgHoldingDays = if (holdingDays.isNotEmpty()) {
            holdingDays.average()
        } else 0.0

        // Best and worst symbols
        val symbolPL = closedTrades.groupBy { it.symbol }
            .mapValues { entry -> entry.value.sumOf { it.profitLoss } }
        val bestSymbol = symbolPL.maxByOrNull { it.value }?.key ?: ""
        val worstSymbol = symbolPL.minByOrNull { it.value }?.key ?: ""

        return AnalyticsSummary(
            totalTrades = allTrades.size,
            winningTrades = winningTrades.size,
            losingTrades = losingTrades.size,
            totalProfitLoss = totalPL,
            winRate = winRate,
            avgWin = avgWin,
            avgLoss = avgLoss,
            profitFactor = profitFactor,
            largestWin = largestWin,
            largestLoss = largestLoss,
            avgHoldingDays = avgHoldingDays,
            openTrades = openTrades.size,
            totalFees = totalFees,
            bestSymbol = bestSymbol,
            worstSymbol = worstSymbol
        )
    }

    /**
     * Export all trades as CSV string.
     */
    // PUBLIC_INTERFACE
    fun exportTradesToCsv(): String {
        val trades = tradeDao.getAllTrades()
        val sb = StringBuilder()
        sb.appendLine("ID,Symbol,Type,Asset Class,Entry Price,Exit Price,Quantity,Entry Date,Exit Date,Stop Loss,Take Profit,Fees,Notes,Tags,Strategy,Status,Emotion,P&L")
        for (trade in trades) {
            sb.appendLine("${trade.id},${trade.symbol},${trade.tradeType},${trade.assetClass},${trade.entryPrice},${trade.exitPrice},${trade.quantity},${trade.entryDate},${trade.exitDate},${trade.stopLoss},${trade.takeProfit},${trade.fees},\"${trade.notes}\",\"${trade.tags}\",${trade.strategy},${trade.status},${trade.emotion},${trade.profitLoss}")
        }
        return sb.toString()
    }

    /**
     * Import trades from CSV string.
     * @return number of trades imported
     */
    // PUBLIC_INTERFACE
    fun importTradesFromCsv(csvContent: String): Int {
        val lines = csvContent.lines().filter { it.isNotBlank() }
        if (lines.size < 2) return 0

        var importedCount = 0
        // Skip header line
        for (i in 1 until lines.size) {
            try {
                val parts = parseCsvLine(lines[i])
                if (parts.size >= 16) {
                    val trade = Trade(
                        symbol = parts[1].trim(),
                        tradeType = parts[2].trim(),
                        assetClass = parts[3].trim(),
                        entryPrice = parts[4].trim().toDoubleOrNull() ?: 0.0,
                        exitPrice = parts[5].trim().toDoubleOrNull() ?: 0.0,
                        quantity = parts[6].trim().toDoubleOrNull() ?: 0.0,
                        entryDate = parts[7].trim(),
                        exitDate = parts[8].trim(),
                        stopLoss = parts[9].trim().toDoubleOrNull() ?: 0.0,
                        takeProfit = parts[10].trim().toDoubleOrNull() ?: 0.0,
                        fees = parts[11].trim().toDoubleOrNull() ?: 0.0,
                        notes = parts[12].trim().removeSurrounding("\""),
                        tags = parts[13].trim().removeSurrounding("\""),
                        strategy = parts[14].trim(),
                        status = parts[15].trim(),
                        emotion = if (parts.size > 16) parts[16].trim() else "Neutral",
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    tradeDao.insertTrade(trade)
                    importedCount++
                }
            } catch (e: Exception) {
                // Skip invalid lines
                continue
            }
        }
        return importedCount
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        for (char in line) {
            when {
                char == '"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current = StringBuilder()
                }
                else -> current.append(char)
            }
        }
        result.add(current.toString())
        return result
    }
}
