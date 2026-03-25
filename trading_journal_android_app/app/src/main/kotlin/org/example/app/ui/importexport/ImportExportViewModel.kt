package org.example.app.ui.importexport

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.example.app.TradingJournalApp
import org.example.app.data.local.TradeDao
import org.example.app.data.repository.TradeRepository
import java.util.concurrent.Executors

/**
 * ViewModel for Import/Export screen.
 * Handles CSV import and export operations.
 */
// PUBLIC_INTERFACE
class ImportExportViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TradeRepository
    private val executor = Executors.newSingleThreadExecutor()

    private val _exportResult = MutableLiveData<String?>()
    /** Observable export result (CSV content or null) */
    val exportResult: LiveData<String?> = _exportResult

    private val _importResult = MutableLiveData<Int>()
    /** Observable import result (number of trades imported) */
    val importResult: LiveData<Int> = _importResult

    private val _statusMessage = MutableLiveData<String>()
    /** Observable status message for user feedback */
    val statusMessage: LiveData<String> = _statusMessage

    private val _isLoading = MutableLiveData(false)
    /** Whether an operation is in progress */
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        val app = application as TradingJournalApp
        val tradeDao = TradeDao(app.database)
        repository = TradeRepository(tradeDao)
    }

    /**
     * Export all trades to CSV string.
     */
    // PUBLIC_INTERFACE
    fun exportTrades() {
        _isLoading.postValue(true)
        executor.execute {
            try {
                val csv = repository.exportTradesToCsv()
                _exportResult.postValue(csv)
                _statusMessage.postValue("Export complete! ${repository.getAllTrades().size} trades exported.")
            } catch (e: Exception) {
                _statusMessage.postValue("Export failed: ${e.message}")
                _exportResult.postValue(null)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Import trades from CSV content.
     */
    // PUBLIC_INTERFACE
    fun importTrades(csvContent: String) {
        _isLoading.postValue(true)
        executor.execute {
            try {
                val count = repository.importTradesFromCsv(csvContent)
                _importResult.postValue(count)
                _statusMessage.postValue("Import complete! $count trades imported.")
            } catch (e: Exception) {
                _statusMessage.postValue("Import failed: ${e.message}")
                _importResult.postValue(0)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Delete all trades (for reset/clear).
     */
    // PUBLIC_INTERFACE
    fun deleteAllTrades() {
        _isLoading.postValue(true)
        executor.execute {
            try {
                val count = repository.deleteAllTrades()
                _statusMessage.postValue("Deleted $count trades.")
            } catch (e: Exception) {
                _statusMessage.postValue("Delete failed: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
