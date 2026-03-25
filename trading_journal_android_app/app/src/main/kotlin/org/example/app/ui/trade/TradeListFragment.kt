package org.example.app.ui.trade

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import org.example.app.R
import org.example.app.ui.adapter.TradeAdapter

/**
 * Fragment displaying the list of all trades with search and filter capabilities.
 */
// PUBLIC_INTERFACE
class TradeListFragment : Fragment() {

    private lateinit var viewModel: TradeListViewModel
    private lateinit var tradeAdapter: TradeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trade_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[TradeListViewModel::class.java]

        val etSearch = view.findViewById<TextInputEditText>(R.id.etSearch)
        val chipAll = view.findViewById<Chip>(R.id.chipAll)
        val chipOpen = view.findViewById<Chip>(R.id.chipOpen)
        val chipClosed = view.findViewById<Chip>(R.id.chipClosed)
        val rvTrades = view.findViewById<RecyclerView>(R.id.rvTrades)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        // Setup RecyclerView
        tradeAdapter = TradeAdapter { trade ->
            val intent = Intent(requireContext(), TradeDetailActivity::class.java)
            intent.putExtra("trade_id", trade.id)
            startActivity(intent)
        }
        rvTrades.layoutManager = LinearLayoutManager(requireContext())
        rvTrades.adapter = tradeAdapter

        // Observe trades
        viewModel.trades.observe(viewLifecycleOwner) { trades ->
            tradeAdapter.updateTrades(trades)
            tvEmpty.visibility = if (trades.isEmpty()) View.VISIBLE else View.GONE
            rvTrades.visibility = if (trades.isEmpty()) View.GONE else View.VISIBLE
        }

        // Observe loading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Search functionality
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                if (query.isNotEmpty()) {
                    viewModel.searchTrades(query)
                } else {
                    viewModel.loadTrades()
                }
            }
        })

        // Filter chips
        chipAll.setOnClickListener {
            chipAll.isChecked = true
            viewModel.setFilter("ALL")
        }
        chipOpen.setOnClickListener {
            chipOpen.isChecked = true
            viewModel.setFilter("OPEN")
        }
        chipClosed.setOnClickListener {
            chipClosed.isChecked = true
            viewModel.setFilter("CLOSED")
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTrades()
    }
}
