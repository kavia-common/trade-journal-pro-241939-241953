package org.example.app.ui.analytics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.example.app.R
import org.example.app.ui.adapter.SymbolPerformanceAdapter
import java.util.Locale

/**
 * Fragment displaying detailed analytics and performance metrics.
 */
// PUBLIC_INTERFACE
class AnalyticsFragment : Fragment() {

    private lateinit var viewModel: AnalyticsViewModel
    private lateinit var symbolAdapter: SymbolPerformanceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analytics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AnalyticsViewModel::class.java]

        val tvEmpty = view.findViewById<TextView>(R.id.tvEmptyAnalytics)
        val cardPerformance = view.findViewById<View>(R.id.cardPerformance)
        val cardSymbol = view.findViewById<View>(R.id.cardSymbolPerformance)

        val tvWinningTrades = view.findViewById<TextView>(R.id.tvWinningTrades)
        val tvLosingTrades = view.findViewById<TextView>(R.id.tvLosingTrades)
        val tvWinRate = view.findViewById<TextView>(R.id.tvWinRate)
        val tvAvgWin = view.findViewById<TextView>(R.id.tvAvgWin)
        val tvAvgLoss = view.findViewById<TextView>(R.id.tvAvgLoss)
        val tvProfitFactor = view.findViewById<TextView>(R.id.tvProfitFactor)
        val tvLargestWin = view.findViewById<TextView>(R.id.tvLargestWin)
        val tvLargestLoss = view.findViewById<TextView>(R.id.tvLargestLoss)
        val tvAvgHolding = view.findViewById<TextView>(R.id.tvAvgHolding)
        val tvTotalFees = view.findViewById<TextView>(R.id.tvTotalFees)
        val tvBestSymbol = view.findViewById<TextView>(R.id.tvBestSymbol)
        val tvWorstSymbol = view.findViewById<TextView>(R.id.tvWorstSymbol)

        // Setup symbol performance RecyclerView
        symbolAdapter = SymbolPerformanceAdapter()
        val rvSymbol = view.findViewById<RecyclerView>(R.id.rvSymbolPerformance)
        rvSymbol.layoutManager = LinearLayoutManager(requireContext())
        rvSymbol.adapter = symbolAdapter

        // Observe summary
        viewModel.summary.observe(viewLifecycleOwner) { summary ->
            val hasData = summary.winningTrades > 0 || summary.losingTrades > 0
            tvEmpty.visibility = if (hasData) View.GONE else View.VISIBLE
            cardPerformance.visibility = if (hasData) View.VISIBLE else View.GONE
            cardSymbol.visibility = if (hasData) View.VISIBLE else View.GONE

            if (hasData) {
                tvWinningTrades.text = summary.winningTrades.toString()
                tvLosingTrades.text = summary.losingTrades.toString()
                tvWinRate.text = String.format(Locale.US, "%.1f%%", summary.winRate)
                tvAvgWin.text = String.format(Locale.US, "$%.2f", summary.avgWin)
                tvAvgLoss.text = String.format(Locale.US, "$%.2f", summary.avgLoss)
                tvProfitFactor.text = String.format(Locale.US, "%.2f", summary.profitFactor)
                tvLargestWin.text = String.format(Locale.US, "$%.2f", summary.largestWin)
                tvLargestLoss.text = String.format(Locale.US, "$%.2f", summary.largestLoss)
                tvAvgHolding.text = String.format(Locale.US, "%.1f", summary.avgHoldingDays)
                tvTotalFees.text = String.format(Locale.US, "$%.2f", summary.totalFees)
                tvBestSymbol.text = if (summary.bestSymbol.isNotEmpty()) summary.bestSymbol else "-"
                tvWorstSymbol.text = if (summary.worstSymbol.isNotEmpty()) summary.worstSymbol else "-"
            }
        }

        // Observe symbol performance
        viewModel.symbolPerformance.observe(viewLifecycleOwner) { perfMap ->
            symbolAdapter.updateData(perfMap)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAnalytics()
    }
}
