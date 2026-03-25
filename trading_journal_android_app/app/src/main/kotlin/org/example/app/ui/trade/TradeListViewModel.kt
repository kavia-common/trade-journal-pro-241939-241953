package org.example.app.ui.trade

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.example.app.TradingJournalApp
import org.example.app.data.local.TradeDao
import org.example.app.data.model.Trade
import org.example.app.data.repository.TradeRepository
import java.util.concurrent.Executors

/**
 * ViewModel for the trade list screen.
 * Manages trade data and filtering state.
 */
// PUBLIC_INTERFACE
class TradeListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TradeRepository
    private val executor = Executors.newSingleThreadExecutor()

    private val _trades = MutableLiveData<List<Trade>>()
    /** Observable list of trades */
    val trades: LiveData<List<Trade>> = _trades

    private val _isLoading = MutableLiveData(false)
    /** Whether data is currently loading */
    val isLoading: LiveData<Boolean> = _isLoading

    private val _filterStatus = MutableLiveData("ALL")
    /** Current filter status */
    val filterStatus: LiveData<String> = _filterStatus

    init {
        val app = application as TradingJournalApp
        val tradeDao = TradeDao(app.database)
        repository = TradeRepository(tradeDao)
        loadTrades()
    }

    /**
     * Load all trades from database.
     */
    // PUBLIC_INTERFACE
    fun loadTrades() {
        _isLoading.postValue(true)
        executor.execute {
            try {
                val filter = _filterStatus.value ?: "ALL"
                val result = if (filter == "ALL") {
                    repository.getAllTrades()
                } else {
                    repository.getTradesByStatus(filter)
                }
                _trades.postValue(result)
            } catch (e: Exception) {
                _trades.postValue(emptyList())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Set the filter status and reload trades.
     */
    // PUBLIC_INTERFACE
    fun setFilter(status: String) {
        _filterStatus.value = status
        loadTrades()
    }

    /**
     * Search trades by symbol.
     */
    // PUBLIC_INTERFACE
    fun searchTrades(query: String) {
        _isLoading.postValue(true)
        executor.execute {
            try {
                val result = if (query.isBlank()) {
                    repository.getAllTrades()
                } else {
                    repository.searchTrades(query)
                }
                _trades.postValue(result)
            } catch (e: Exception) {
                _trades.postValue(emptyList())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Delete a trade.
     */
    // PUBLIC_INTERFACE
    fun deleteTrade(tradeId: Long) {
        executor.execute {
            repository.deleteTrade(tradeId)
            loadTrades()
        }
    }

    /**
     * Get trades for a specific date.
     */
    // PUBLIC_INTERFACE
    fun loadTradesForDate(date: String) {
        _isLoading.postValue(true)
        executor.execute {
            try {
                val result = repository.getTradesByDate(date)
                _trades.postValue(result)
            } catch (e: Exception) {
                _trades.postValue(emptyList())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Get distinct trade dates for calendar.
     */
    // PUBLIC_INTERFACE
    fun getTradeDates(): List<String> {
        return try {
            repository.getDistinctTradeDates()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
