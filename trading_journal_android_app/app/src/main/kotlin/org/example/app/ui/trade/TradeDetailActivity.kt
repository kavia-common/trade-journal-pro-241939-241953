package org.example.app.ui.trade

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import org.example.app.R
import org.example.app.TradingJournalApp
import org.example.app.data.local.TradeDao
import org.example.app.data.model.Trade
import java.util.Locale
import java.util.concurrent.Executors

/**
 * Activity displaying the full details of a single trade,
 * with options to edit or delete.
 */
// PUBLIC_INTERFACE
class TradeDetailActivity : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private var tradeId: Long = -1L
    private var currentTrade: Trade? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade_detail)

        tradeId = intent.getLongExtra("trade_id", -1L)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val btnEdit = findViewById<MaterialButton>(R.id.btnEdit)
        val btnDelete = findViewById<MaterialButton>(R.id.btnDelete)

        // Edit button
        btnEdit.setOnClickListener {
            val intent = Intent(this, AddEditTradeActivity::class.java)
            intent.putExtra("trade_id", tradeId)
            startActivity(intent)
        }

        // Delete button with confirmation
        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.delete_trade)
                .setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.yes) { _, _ ->
                    deleteTrade()
                }
                .setNegativeButton(R.string.no, null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadTradeDetail()
    }

    private fun loadTradeDetail() {
        if (tradeId <= 0) {
            Toast.makeText(this, "Invalid trade", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        executor.execute {
            val app = application as TradingJournalApp
            val dao = TradeDao(app.database)
            val trade = dao.getTradeById(tradeId)
            if (trade != null) {
                currentTrade = trade
                runOnUiThread { displayTrade(trade) }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "Trade not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun displayTrade(trade: Trade) {
        // Symbol and header
        findViewById<TextView>(R.id.tvSymbol).text = trade.symbol

        // Trade type
        val tvTradeType = findViewById<TextView>(R.id.tvTradeType)
        tvTradeType.text = trade.tradeType
        val typeColor = if (trade.tradeType == "BUY") R.color.profit_green else R.color.loss_red
        tvTradeType.setTextColor(ContextCompat.getColor(this, typeColor))

        // Asset class
        findViewById<TextView>(R.id.tvAssetClass).text = trade.assetClass

        // Status
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        tvStatus.text = trade.status
        when (trade.status) {
            "OPEN" -> tvStatus.setTextColor(ContextCompat.getColor(this, R.color.primary))
            "CLOSED" -> tvStatus.setTextColor(ContextCompat.getColor(this, R.color.secondary))
            else -> tvStatus.setTextColor(ContextCompat.getColor(this, R.color.text_hint))
        }

        // P&L
        val tvPnl = findViewById<TextView>(R.id.tvPnl)
        val tvPnlPercent = findViewById<TextView>(R.id.tvPnlPercent)
        if (trade.status == "CLOSED") {
            val pnl = trade.profitLoss
            val pnlPct = trade.profitLossPercent
            tvPnl.text = String.format(Locale.US, "$%.2f", pnl)
            tvPnlPercent.text = String.format(Locale.US, "%.2f%%", pnlPct)
            val pnlColor = if (pnl >= 0) R.color.profit_green else R.color.loss_red
            tvPnl.setTextColor(ContextCompat.getColor(this, pnlColor))
            tvPnlPercent.setTextColor(ContextCompat.getColor(this, pnlColor))
        } else {
            tvPnl.text = "—"
            tvPnlPercent.text = "Open position"
            tvPnl.setTextColor(ContextCompat.getColor(this, R.color.text_hint))
            tvPnlPercent.setTextColor(ContextCompat.getColor(this, R.color.text_hint))
        }

        // Details
        findViewById<TextView>(R.id.tvEntryPrice).text = String.format(Locale.US, "$%.2f", trade.entryPrice)
        findViewById<TextView>(R.id.tvExitPrice).text = if (trade.exitPrice > 0) {
            String.format(Locale.US, "$%.2f", trade.exitPrice)
        } else "—"
        findViewById<TextView>(R.id.tvQuantity).text = String.format(Locale.US, "%.2f", trade.quantity)
        findViewById<TextView>(R.id.tvFees).text = String.format(Locale.US, "$%.2f", trade.fees)
        findViewById<TextView>(R.id.tvEntryDate).text = trade.entryDate.ifEmpty { "—" }
        findViewById<TextView>(R.id.tvExitDate).text = trade.exitDate.ifEmpty { "—" }
        findViewById<TextView>(R.id.tvStopLoss).text = if (trade.stopLoss > 0) {
            String.format(Locale.US, "$%.2f", trade.stopLoss)
        } else "—"
        findViewById<TextView>(R.id.tvTakeProfit).text = if (trade.takeProfit > 0) {
            String.format(Locale.US, "$%.2f", trade.takeProfit)
        } else "—"
        findViewById<TextView>(R.id.tvStrategy).text = trade.strategy.ifEmpty { "—" }
        findViewById<TextView>(R.id.tvEmotion).text = trade.emotion.ifEmpty { "—" }
        findViewById<TextView>(R.id.tvTags).text = trade.tags.ifEmpty { "—" }
        findViewById<TextView>(R.id.tvNotes).text = trade.notes.ifEmpty { "—" }
    }

    private fun deleteTrade() {
        executor.execute {
            val app = application as TradingJournalApp
            val dao = TradeDao(app.database)
            dao.deleteTrade(tradeId)
            runOnUiThread {
                Toast.makeText(this, "Trade deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
