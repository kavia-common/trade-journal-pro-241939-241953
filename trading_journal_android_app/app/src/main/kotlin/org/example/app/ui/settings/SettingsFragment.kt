package org.example.app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.example.app.R

/**
 * Fragment for app settings including API URL, default asset class, and currency.
 */
// PUBLIC_INTERFACE
class SettingsFragment : Fragment() {

    private lateinit var viewModel: SettingsViewModel

    private val assetClasses = arrayOf("Stock", "Option", "Forex", "Crypto", "Futures")
    private val currencies = arrayOf("USD", "EUR", "GBP", "JPY", "INR")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        val etApiUrl = view.findViewById<TextInputEditText>(R.id.etApiUrl)
        val spinnerAssetClass = view.findViewById<Spinner>(R.id.spinnerAssetClass)
        val spinnerCurrency = view.findViewById<Spinner>(R.id.spinnerCurrency)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSaveSettings)
        val tvStatus = view.findViewById<TextView>(R.id.tvSettingsStatus)

        // Setup spinners
        val assetAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            assetClasses
        )
        assetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAssetClass.adapter = assetAdapter

        val currencyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            currencies
        )
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency.adapter = currencyAdapter

        // Observe settings
        viewModel.apiBaseUrl.observe(viewLifecycleOwner) { url ->
            etApiUrl.setText(url)
        }

        viewModel.defaultAssetClass.observe(viewLifecycleOwner) { assetClass ->
            val index = assetClasses.indexOf(assetClass)
            if (index >= 0) spinnerAssetClass.setSelection(index)
        }

        viewModel.currency.observe(viewLifecycleOwner) { currency ->
            val index = currencies.indexOf(currency)
            if (index >= 0) spinnerCurrency.setSelection(index)
        }

        viewModel.statusMessage.observe(viewLifecycleOwner) { message ->
            tvStatus.text = message
            tvStatus.visibility = View.VISIBLE
        }

        // Save button
        btnSave.setOnClickListener {
            val url = etApiUrl.text?.toString() ?: ""
            val assetClass = spinnerAssetClass.selectedItem?.toString() ?: "Stock"
            val currency = spinnerCurrency.selectedItem?.toString() ?: "USD"

            viewModel.saveApiBaseUrl(url)
            viewModel.saveDefaultAssetClass(assetClass)
            viewModel.saveCurrency(currency)
        }
    }
}
