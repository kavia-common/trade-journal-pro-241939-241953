package org.example.app.ui.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.example.app.TradingJournalApp
import org.example.app.data.local.TradeDao
import org.example.app.data.model.AnalyticsSummary
import org.example.app.data.model.Trade
import org.example.app.data.repository.TradeRepository
import java.util.concurrent.Executors

/**
 * ViewModel for the Analytics screen.
 * Provides detailed analytics and performance metrics.
 */
// PUBLIC_INTERFACE
class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TradeRepository
    private val executor = Executors.newSingleThreadExecutor()

    private val _summary = MutableLiveData<AnalyticsSummary>()
    /** Observable analytics summary */
    val summary: LiveData<AnalyticsSummary> = _summary

    private val _trades = MutableLiveData<List<Trade>>()
    /** Observable list of all trades for chart data */
    val trades: LiveData<List<Trade>> = _trades

    private val _symbolPerformance = MutableLiveData<Map<String, Double>>()
    /** Symbol-wise performance map */
    val symbolPerformance: LiveData<Map<String, Double>> = _symbolPerformance

    private val _isLoading = MutableLiveData(false)
    /** Whether data is loading */
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        val app = application as TradingJournalApp
        val tradeDao = TradeDao(app.database)
        repository = TradeRepository(tradeDao)
        loadAnalytics()
    }

    /**
     * Load all analytics data.
     */
    // PUBLIC_INTERFACE
    fun loadAnalytics() {
        _isLoading.postValue(true)
        executor.execute {
            try {
                val analyticsSummary = repository.getAnalyticsSummary()
                _summary.postValue(analyticsSummary)

                val allTrades = repository.getAllTrades()
                _trades.postValue(allTrades)

                // Calculate symbol performance
                val closedTrades = allTrades.filter { it.status == "CLOSED" }
                val perfMap = closedTrades.groupBy { it.symbol }
                    .mapValues { entry -> entry.value.sumOf { it.profitLoss } }
                _symbolPerformance.postValue(perfMap)
            } catch (e: Exception) {
                _summary.postValue(AnalyticsSummary())
                _trades.postValue(emptyList())
                _symbolPerformance.postValue(emptyMap())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
