package org.example.app.ui.trade

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.example.app.R
import org.example.app.TradingJournalApp
import org.example.app.data.local.TradeDao
import org.example.app.data.model.Trade
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.Executors

/**
 * Activity for adding a new trade or editing an existing one.
 */
// PUBLIC_INTERFACE
class AddEditTradeActivity : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private var tradeId: Long = -1L
    private var existingTrade: Trade? = null

    private val tradeTypes = arrayOf("BUY", "SELL")
    private val assetClasses = arrayOf("Stock", "Option", "Forex", "Crypto", "Futures")
    private val statuses = arrayOf("OPEN", "CLOSED", "CANCELLED")
    private val emotions = arrayOf("Neutral", "Confident", "Fearful", "Greedy", "Anxious", "Calm")

    private lateinit var etSymbol: TextInputEditText
    private lateinit var spinnerTradeType: Spinner
    private lateinit var spinnerAssetClass: Spinner
    private lateinit var etEntryPrice: TextInputEditText
    private lateinit var etExitPrice: TextInputEditText
    private lateinit var etQuantity: TextInputEditText
    private lateinit var etEntryDate: TextInputEditText
    private lateinit var etExitDate: TextInputEditText
    private lateinit var etStopLoss: TextInputEditText
    private lateinit var etTakeProfit: TextInputEditText
    private lateinit var etFees: TextInputEditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var spinnerEmotion: Spinner
    private lateinit var etStrategy: TextInputEditText
    private lateinit var etTags: TextInputEditText
    private lateinit var etNotes: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_trade)

        // Get trade ID if editing
        tradeId = intent.getLongExtra("trade_id", -1L)

        // Initialize views
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        etSymbol = findViewById(R.id.etSymbol)
        spinnerTradeType = findViewById(R.id.spinnerTradeType)
        spinnerAssetClass = findViewById(R.id.spinnerAssetClass)
        etEntryPrice = findViewById(R.id.etEntryPrice)
        etExitPrice = findViewById(R.id.etExitPrice)
        etQuantity = findViewById(R.id.etQuantity)
        etEntryDate = findViewById(R.id.etEntryDate)
        etExitDate = findViewById(R.id.etExitDate)
        etStopLoss = findViewById(R.id.etStopLoss)
        etTakeProfit = findViewById(R.id.etTakeProfit)
        etFees = findViewById(R.id.etFees)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        spinnerEmotion = findViewById(R.id.spinnerEmotion)
        etStrategy = findViewById(R.id.etStrategy)
        etTags = findViewById(R.id.etTags)
        etNotes = findViewById(R.id.etNotes)
        val btnSave = findViewById<MaterialButton>(R.id.btnSave)

        // Setup toolbar
        toolbar.title = if (tradeId > 0) getString(R.string.edit_trade) else getString(R.string.add_trade)
        toolbar.setNavigationOnClickListener { finish() }

        // Setup spinners
        setupSpinner(spinnerTradeType, tradeTypes)
        setupSpinner(spinnerAssetClass, assetClasses)
        setupSpinner(spinnerStatus, statuses)
        setupSpinner(spinnerEmotion, emotions)

        // Setup date pickers
        etEntryDate.setOnClickListener { showDatePicker(etEntryDate) }
        etExitDate.setOnClickListener { showDatePicker(etExitDate) }

        // If editing, load existing trade data
        if (tradeId > 0) {
            loadExistingTrade()
        } else {
            // Set today's date as default entry date
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(System.currentTimeMillis())
            etEntryDate.setText(today)
        }

        // Save button
        btnSave.setOnClickListener { saveTrade() }
    }

    private fun setupSpinner(spinner: Spinner, items: Array<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun showDatePicker(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val date = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                editText.setText(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun loadExistingTrade() {
        executor.execute {
            val app = application as TradingJournalApp
            val dao = TradeDao(app.database)
            val trade = dao.getTradeById(tradeId)
            if (trade != null) {
                existingTrade = trade
                runOnUiThread { populateFields(trade) }
            }
        }
    }

    private fun populateFields(trade: Trade) {
        etSymbol.setText(trade.symbol)
        spinnerTradeType.setSelection(tradeTypes.indexOf(trade.tradeType).coerceAtLeast(0))
        spinnerAssetClass.setSelection(assetClasses.indexOf(trade.assetClass).coerceAtLeast(0))
        etEntryPrice.setText(if (trade.entryPrice > 0) trade.entryPrice.toString() else "")
        etExitPrice.setText(if (trade.exitPrice > 0) trade.exitPrice.toString() else "")
        etQuantity.setText(if (trade.quantity > 0) trade.quantity.toString() else "")
        etEntryDate.setText(trade.entryDate)
        etExitDate.setText(trade.exitDate)
        etStopLoss.setText(if (trade.stopLoss > 0) trade.stopLoss.toString() else "")
        etTakeProfit.setText(if (trade.takeProfit > 0) trade.takeProfit.toString() else "")
        etFees.setText(if (trade.fees > 0) trade.fees.toString() else "")
        spinnerStatus.setSelection(statuses.indexOf(trade.status).coerceAtLeast(0))
        spinnerEmotion.setSelection(emotions.indexOf(trade.emotion).coerceAtLeast(0))
        etStrategy.setText(trade.strategy)
        etTags.setText(trade.tags)
        etNotes.setText(trade.notes)
    }

    private fun saveTrade() {
        val symbol = etSymbol.text?.toString()?.trim()?.uppercase(Locale.US) ?: ""
        if (symbol.isEmpty()) {
            etSymbol.error = "Symbol is required"
            etSymbol.requestFocus()
            return
        }

        val entryPriceText = etEntryPrice.text?.toString()?.trim() ?: ""
        val entryPrice = entryPriceText.toDoubleOrNull() ?: 0.0
        if (entryPrice <= 0) {
            etEntryPrice.error = "Valid entry price required"
            etEntryPrice.requestFocus()
            return
        }

        val quantityText = etQuantity.text?.toString()?.trim() ?: ""
        val quantity = quantityText.toDoubleOrNull() ?: 0.0
        if (quantity <= 0) {
            etQuantity.error = "Valid quantity required"
            etQuantity.requestFocus()
            return
        }

        val entryDate = etEntryDate.text?.toString()?.trim() ?: ""
        if (entryDate.isEmpty()) {
            etEntryDate.error = "Entry date required"
            return
        }

        val trade = Trade(
            id = if (tradeId > 0) tradeId else 0L,
            symbol = symbol,
            tradeType = spinnerTradeType.selectedItem?.toString() ?: "BUY",
            assetClass = spinnerAssetClass.selectedItem?.toString() ?: "Stock",
            entryPrice = entryPrice,
            exitPrice = etExitPrice.text?.toString()?.toDoubleOrNull() ?: 0.0,
            quantity = quantity,
            entryDate = entryDate,
            exitDate = etExitDate.text?.toString()?.trim() ?: "",
            stopLoss = etStopLoss.text?.toString()?.toDoubleOrNull() ?: 0.0,
            takeProfit = etTakeProfit.text?.toString()?.toDoubleOrNull() ?: 0.0,
            fees = etFees.text?.toString()?.toDoubleOrNull() ?: 0.0,
            notes = etNotes.text?.toString()?.trim() ?: "",
            tags = etTags.text?.toString()?.trim() ?: "",
            strategy = etStrategy.text?.toString()?.trim() ?: "",
            status = spinnerStatus.selectedItem?.toString() ?: "OPEN",
            emotion = spinnerEmotion.selectedItem?.toString() ?: "Neutral",
            createdAt = existingTrade?.createdAt ?: System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        executor.execute {
            val app = application as TradingJournalApp
            val dao = TradeDao(app.database)
            if (tradeId > 0) {
                dao.updateTrade(trade)
            } else {
                dao.insertTrade(trade)
            }
            runOnUiThread {
                Toast.makeText(this, "Trade saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
