package org.example.app.ui.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * ViewModel for the Settings screen.
 * Manages app preferences using SharedPreferences.
 */
// PUBLIC_INTERFACE
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("trading_journal_prefs", Context.MODE_PRIVATE)

    private val _apiBaseUrl = MutableLiveData<String>()
    /** Observable API base URL */
    val apiBaseUrl: LiveData<String> = _apiBaseUrl

    private val _defaultAssetClass = MutableLiveData<String>()
    /** Observable default asset class */
    val defaultAssetClass: LiveData<String> = _defaultAssetClass

    private val _currency = MutableLiveData<String>()
    /** Observable currency setting */
    val currency: LiveData<String> = _currency

    private val _statusMessage = MutableLiveData<String>()
    /** Observable status message for user feedback */
    val statusMessage: LiveData<String> = _statusMessage

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _apiBaseUrl.value = prefs.getString("api_base_url", "http://10.0.2.2:3001") ?: "http://10.0.2.2:3001"
        _defaultAssetClass.value = prefs.getString("default_asset_class", "Stock") ?: "Stock"
        _currency.value = prefs.getString("currency", "USD") ?: "USD"
    }

    /**
     * Save API base URL.
     */
    // PUBLIC_INTERFACE
    fun saveApiBaseUrl(url: String) {
        prefs.edit().putString("api_base_url", url).apply()
        _apiBaseUrl.value = url
        _statusMessage.value = "API URL saved"
    }

    /**
     * Save default asset class.
     */
    // PUBLIC_INTERFACE
    fun saveDefaultAssetClass(assetClass: String) {
        prefs.edit().putString("default_asset_class", assetClass).apply()
        _defaultAssetClass.value = assetClass
        _statusMessage.value = "Default asset class saved"
    }

    /**
     * Save currency preference.
     */
    // PUBLIC_INTERFACE
    fun saveCurrency(currency: String) {
        prefs.edit().putString("currency", currency).apply()
        _currency.value = currency
        _statusMessage.value = "Currency saved"
    }

    /**
     * Get the stored currency symbol.
     */
    // PUBLIC_INTERFACE
    fun getCurrencySymbol(): String {
        return when (prefs.getString("currency", "USD")) {
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "JPY" -> "¥"
            "INR" -> "₹"
            else -> "$"
        }
    }
}
