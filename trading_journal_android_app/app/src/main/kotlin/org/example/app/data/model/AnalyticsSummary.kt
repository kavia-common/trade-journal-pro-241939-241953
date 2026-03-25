package org.example.app.data.model

/**
 * Aggregated analytics summary computed from trades.
 */
// PUBLIC_INTERFACE
data class AnalyticsSummary(
    /** Total number of trades */
    val totalTrades: Int = 0,
    /** Number of winning trades */
    val winningTrades: Int = 0,
    /** Number of losing trades */
    val losingTrades: Int = 0,
    /** Total profit/loss across all closed trades */
    val totalProfitLoss: Double = 0.0,
    /** Win rate as a percentage */
    val winRate: Double = 0.0,
    /** Average profit on winning trades */
    val avgWin: Double = 0.0,
    /** Average loss on losing trades */
    val avgLoss: Double = 0.0,
    /** Profit factor (total gains / total losses) */
    val profitFactor: Double = 0.0,
    /** Largest single win */
    val largestWin: Double = 0.0,
    /** Largest single loss */
    val largestLoss: Double = 0.0,
    /** Average holding period in days */
    val avgHoldingDays: Double = 0.0,
    /** Total number of open trades */
    val openTrades: Int = 0,
    /** Total fees paid */
    val totalFees: Double = 0.0,
    /** Best performing symbol */
    val bestSymbol: String = "",
    /** Worst performing symbol */
    val worstSymbol: String = ""
)
