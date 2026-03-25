package org.example.app.data.model

import java.io.Serializable

/**
 * Represents a single trade entry in the trading journal.
 * This is both the Room entity and the domain model.
 */
// PUBLIC_INTERFACE
data class Trade(
    /** Unique identifier for the trade */
    val id: Long = 0L,
    /** Trading symbol/ticker (e.g., AAPL, TSLA) */
    val symbol: String = "",
    /** Trade type: BUY or SELL */
    val tradeType: String = "BUY",
    /** Asset class: Stock, Option, Forex, Crypto, Futures */
    val assetClass: String = "Stock",
    /** Entry price per unit */
    val entryPrice: Double = 0.0,
    /** Exit price per unit (0 if still open) */
    val exitPrice: Double = 0.0,
    /** Number of units/shares */
    val quantity: Double = 0.0,
    /** Date the trade was opened (ISO format YYYY-MM-DD) */
    val entryDate: String = "",
    /** Date the trade was closed (empty if still open) */
    val exitDate: String = "",
    /** Stop loss price */
    val stopLoss: Double = 0.0,
    /** Take profit price */
    val takeProfit: Double = 0.0,
    /** Trading fees/commission */
    val fees: Double = 0.0,
    /** User notes about the trade */
    val notes: String = "",
    /** Tags for categorization (comma-separated) */
    val tags: String = "",
    /** Trading strategy used */
    val strategy: String = "",
    /** Trade status: OPEN, CLOSED, CANCELLED */
    val status: String = "OPEN",
    /** Emotional state during trade: Confident, Fearful, Greedy, Neutral */
    val emotion: String = "Neutral",
    /** Screenshot path (local file path) */
    val screenshotPath: String = "",
    /** Timestamp of creation */
    val createdAt: Long = System.currentTimeMillis(),
    /** Timestamp of last update */
    val updatedAt: Long = System.currentTimeMillis()
) : Serializable {

    /** Calculated profit/loss for the trade */
    val profitLoss: Double
        get() {
            if (exitPrice <= 0.0 || status != "CLOSED") return 0.0
            val gross = if (tradeType == "BUY") {
                (exitPrice - entryPrice) * quantity
            } else {
                (entryPrice - exitPrice) * quantity
            }
            return gross - fees
        }

    /** Calculated profit/loss percentage */
    val profitLossPercent: Double
        get() {
            if (entryPrice <= 0.0 || exitPrice <= 0.0 || status != "CLOSED") return 0.0
            return if (tradeType == "BUY") {
                ((exitPrice - entryPrice) / entryPrice) * 100.0
            } else {
                ((entryPrice - exitPrice) / entryPrice) * 100.0
            }
        }

    /** Whether this trade is profitable */
    val isWinningTrade: Boolean
        get() = profitLoss > 0.0
}
