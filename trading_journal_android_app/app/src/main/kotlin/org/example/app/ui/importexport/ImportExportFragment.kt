package org.example.app.ui.importexport

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import org.example.app.R
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * Fragment for importing and exporting trade data as CSV.
 */
// PUBLIC_INTERFACE
class ImportExportFragment : Fragment() {

    private lateinit var viewModel: ImportExportViewModel

    // Launcher for creating a file (export)
    private val createFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                writeExportToUri(uri)
            }
        }
    }

    // Launcher for opening a file (import)
    private val openFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                readImportFromUri(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_import_export, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ImportExportViewModel::class.java]

        val btnExport = view.findViewById<MaterialButton>(R.id.btnExport)
        val btnImport = view.findViewById<MaterialButton>(R.id.btnImport)
        val btnDeleteAll = view.findViewById<MaterialButton>(R.id.btnDeleteAll)
        val tvStatus = view.findViewById<TextView>(R.id.tvStatusMessage)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        // Export button
        btnExport.setOnClickListener {
            viewModel.exportTrades()
        }

        // Import button
        btnImport.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            openFileLauncher.launch(intent)
        }

        // Delete all button with confirmation
        btnDeleteAll.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_all_trades)
                .setMessage(R.string.confirm_delete_all)
                .setPositiveButton(R.string.yes) { _, _ ->
                    viewModel.deleteAllTrades()
                }
                .setNegativeButton(R.string.no, null)
                .show()
        }

        // Observe export result - prompt save
        viewModel.exportResult.observe(viewLifecycleOwner) { csvContent ->
            if (csvContent != null) {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/csv"
                    putExtra(Intent.EXTRA_TITLE, "trading_journal_export.csv")
                }
                createFileLauncher.launch(intent)
            }
        }

        // Observe status message
        viewModel.statusMessage.observe(viewLifecycleOwner) { message ->
            tvStatus.text = message
            tvStatus.visibility = View.VISIBLE
        }

        // Observe loading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnExport.isEnabled = !isLoading
            btnImport.isEnabled = !isLoading
        }
    }

    private fun writeExportToUri(uri: Uri) {
        try {
            val csvContent = viewModel.exportResult.value ?: return
            requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = OutputStreamWriter(outputStream)
                writer.write(csvContent)
                writer.flush()
            }
            Toast.makeText(requireContext(), "Export saved successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readImportFromUri(uri: Uri) {
        try {
            requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                val csvContent = reader.readText()
                viewModel.importTrades(csvContent)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
