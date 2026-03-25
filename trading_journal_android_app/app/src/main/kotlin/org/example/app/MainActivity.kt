package org.example.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.example.app.ui.analytics.AnalyticsFragment
import org.example.app.ui.dashboard.DashboardFragment
import org.example.app.ui.importexport.ImportExportFragment
import org.example.app.ui.settings.SettingsFragment
import org.example.app.ui.trade.AddEditTradeActivity
import org.example.app.ui.trade.TradeListFragment

/**
 * Main activity serving as the host for all primary fragments.
 * Manages bottom navigation and the floating action button for adding trades.
 */
// PUBLIC_INTERFACE
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabAddTrade: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottomNavigation)
        fabAddTrade = findViewById(R.id.fabAddTrade)

        // Set up bottom navigation
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    loadFragment(DashboardFragment())
                    true
                }
                R.id.nav_trades -> {
                    loadFragment(TradeListFragment())
                    true
                }
                R.id.nav_analytics -> {
                    loadFragment(AnalyticsFragment())
                    true
                }
                R.id.nav_import_export -> {
                    loadFragment(ImportExportFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }

        // FAB click to add new trade
        fabAddTrade.setOnClickListener {
            val intent = Intent(this, AddEditTradeActivity::class.java)
            startActivity(intent)
        }

        // Load dashboard by default
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_dashboard
        }
    }

    /**
     * Replace the current fragment in the container.
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
