package org.example.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.example.app.R
import org.example.app.data.model.Trade
import java.util.Locale

/**
 * RecyclerView adapter for displaying trade items in a list.
 */
// PUBLIC_INTERFACE
class TradeAdapter(
    private var trades: List<Trade> = emptyList(),
    private val onTradeClick: (Trade) -> Unit
) : RecyclerView.Adapter<TradeAdapter.TradeViewHolder>() {

    /**
     * Update the list of trades and refresh the display.
     */
    // PUBLIC_INTERFACE
    fun updateTrades(newTrades: List<Trade>) {
        trades = newTrades
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trade, parent, false)
        return TradeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TradeViewHolder, position: Int) {
        holder.bind(trades[position])
    }

    override fun getItemCount(): Int = trades.size

    inner class TradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSymbol: TextView = itemView.findViewById(R.id.tvSymbol)
        private val tvTradeType: TextView = itemView.findViewById(R.id.tvTradeType)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvAssetClass: TextView = itemView.findViewById(R.id.tvAssetClass)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvPnl: TextView = itemView.findViewById(R.id.tvPnl)
        private val tvPnlPercent: TextView = itemView.findViewById(R.id.tvPnlPercent)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)

        fun bind(trade: Trade) {
            val context = itemView.context

            tvSymbol.text = trade.symbol
            tvTradeType.text = trade.tradeType
            tvAssetClass.text = trade.assetClass
            tvDate.text = trade.entryDate
            tvQuantity.text = String.format(Locale.US, "Qty: %.2f", trade.quantity)

            // Trade type color
            if (trade.tradeType == "BUY") {
                tvTradeType.setTextColor(ContextCompat.getColor(context, R.color.profit_green))
            } else {
                tvTradeType.setTextColor(ContextCompat.getColor(context, R.color.loss_red))
            }

            // Status
            tvStatus.text = trade.status
            when (trade.status) {
                "OPEN" -> tvStatus.setTextColor(ContextCompat.getColor(context, R.color.primary))
                "CLOSED" -> tvStatus.setTextColor(ContextCompat.getColor(context, R.color.secondary))
                else -> tvStatus.setTextColor(ContextCompat.getColor(context, R.color.text_hint))
            }

            // P&L display
            if (trade.status == "CLOSED") {
                val pnl = trade.profitLoss
                val pnlPercent = trade.profitLossPercent
                tvPnl.text = String.format(Locale.US, "$%.2f", pnl)
                tvPnlPercent.text = String.format(Locale.US, "%.2f%%", pnlPercent)

                val pnlColor = if (pnl >= 0) R.color.profit_green else R.color.loss_red
                tvPnl.setTextColor(ContextCompat.getColor(context, pnlColor))
                tvPnlPercent.setTextColor(ContextCompat.getColor(context, pnlColor))
            } else {
                tvPnl.text = "—"
                tvPnlPercent.text = ""
                tvPnl.setTextColor(ContextCompat.getColor(context, R.color.text_hint))
            }

            itemView.setOnClickListener { onTradeClick(trade) }
        }
    }
}
