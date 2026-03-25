package org.example.app.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.example.app.R
import org.example.app.ui.adapter.TradeAdapter
import org.example.app.ui.trade.TradeDetailActivity
import java.util.Locale

/**
 * Fragment displaying the main dashboard with summary statistics
 * and recent trades.
 */
// PUBLIC_INTERFACE
class DashboardFragment : Fragment() {

    private lateinit var viewModel: DashboardViewModel
    private lateinit var tradeAdapter: TradeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        val tvTotalTrades = view.findViewById<TextView>(R.id.tvTotalTrades)
        val tvOpenTrades = view.findViewById<TextView>(R.id.tvOpenTrades)
        val tvWinRate = view.findViewById<TextView>(R.id.tvWinRate)
        val tvTotalPnl = view.findViewById<TextView>(R.id.tvTotalPnl)
        val tvEmptyState = view.findViewById<TextView>(R.id.tvEmptyState)
        val rvRecentTrades = view.findViewById<RecyclerView>(R.id.rvRecentTrades)
        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        // Setup RecyclerView
        tradeAdapter = TradeAdapter { trade ->
            val intent = Intent(requireContext(), TradeDetailActivity::class.java)
            intent.putExtra("trade_id", trade.id)
            startActivity(intent)
        }
        rvRecentTrades.layoutManager = LinearLayoutManager(requireContext())
        rvRecentTrades.adapter = tradeAdapter

        // Observe summary data
        viewModel.summary.observe(viewLifecycleOwner) { summary ->
            tvTotalTrades.text = summary.totalTrades.toString()
            tvOpenTrades.text = summary.openTrades.toString()
            tvWinRate.text = String.format(Locale.US, "%.1f%%", summary.winRate)

            val pnlText = String.format(Locale.US, "$%.2f", summary.totalProfitLoss)
            tvTotalPnl.text = pnlText
            if (summary.totalProfitLoss >= 0) {
                tvTotalPnl.setTextColor(resources.getColor(R.color.profit_green, null))
            } else {
                tvTotalPnl.setTextColor(resources.getColor(R.color.loss_red, null))
            }
        }

        // Observe recent trades
        viewModel.recentTrades.observe(viewLifecycleOwner) { trades ->
            tradeAdapter.updateTrades(trades)
            tvEmptyState.visibility = if (trades.isEmpty()) View.VISIBLE else View.GONE
            rvRecentTrades.visibility = if (trades.isEmpty()) View.GONE else View.VISIBLE
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            swipeRefresh.isRefreshing = isLoading
        }

        // Pull to refresh
        swipeRefresh.setColorSchemeResources(R.color.primary)
        swipeRefresh.setOnRefreshListener {
            viewModel.loadDashboard()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadDashboard()
    }
}
