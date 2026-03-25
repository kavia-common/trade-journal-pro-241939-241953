package org.example.app.ui.dashboard

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
 * ViewModel for the Dashboard screen.
 * Provides summary analytics and recent trades.
 */
// PUBLIC_INTERFACE
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TradeRepository
    private val executor = Executors.newSingleThreadExecutor()

    private val _summary = MutableLiveData<AnalyticsSummary>()
    /** Observable analytics summary */
    val summary: LiveData<AnalyticsSummary> = _summary

    private val _recentTrades = MutableLiveData<List<Trade>>()
    /** Observable list of recent trades */
    val recentTrades: LiveData<List<Trade>> = _recentTrades

    private val _isLoading = MutableLiveData(false)
    /** Whether data is currently loading */
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        val app = application as TradingJournalApp
        val tradeDao = TradeDao(app.database)
        repository = TradeRepository(tradeDao)
        loadDashboard()
    }

    /**
     * Load dashboard data including summary and recent trades.
     */
    // PUBLIC_INTERFACE
    fun loadDashboard() {
        _isLoading.postValue(true)
        executor.execute {
            try {
                val analyticsSummary = repository.getAnalyticsSummary()
                _summary.postValue(analyticsSummary)

                val allTrades = repository.getAllTrades()
                _recentTrades.postValue(allTrades.take(5))
            } catch (e: Exception) {
                _summary.postValue(AnalyticsSummary())
                _recentTrades.postValue(emptyList())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
