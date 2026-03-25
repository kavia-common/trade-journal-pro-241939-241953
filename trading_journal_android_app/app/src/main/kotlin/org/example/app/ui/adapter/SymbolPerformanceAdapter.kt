package org.example.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.example.app.R
import java.util.Locale

/**
 * RecyclerView adapter for displaying symbol performance data.
 */
// PUBLIC_INTERFACE
class SymbolPerformanceAdapter(
    private var data: List<Pair<String, Double>> = emptyList()
) : RecyclerView.Adapter<SymbolPerformanceAdapter.ViewHolder>() {

    /**
     * Update the performance data and refresh.
     */
    // PUBLIC_INTERFACE
    fun updateData(newData: Map<String, Double>) {
        data = newData.entries
            .sortedByDescending { it.value }
            .map { it.key to it.value }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_symbol_performance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSymbolName: TextView = itemView.findViewById(R.id.tvSymbolName)
        private val tvSymbolPnl: TextView = itemView.findViewById(R.id.tvSymbolPnl)

        fun bind(item: Pair<String, Double>) {
            val context = itemView.context
            tvSymbolName.text = item.first
            tvSymbolPnl.text = String.format(Locale.US, "$%.2f", item.second)

            val color = if (item.second >= 0) R.color.profit_green else R.color.loss_red
            tvSymbolPnl.setTextColor(ContextCompat.getColor(context, color))
        }
    }
}
