package com.kasal.podoapp.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PatientHistory
import com.kasal.podoapp.data.PodologiaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PatientHistoryTabsActivity : AppCompatActivity() {

    private var patientId: Int = 0
    private var existingHistory: PatientHistory? = null

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var btnSave: Button
    private lateinit var pagerAdapter: PatientHistoryPagerAdapter

    private val tabTitles = arrayOf(
        "Ιατρικά",
        "Στάση/Παραμορφώσεις",
        "Πόδι – Αριστερό",
        "Πόδι – Δεξί",
        "Οίδημα & Κιρσοί",
        "Ορθωτικά/Νάρθηκας"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_history_tabs)

        val pid = intent.getIntExtra("patientId", -1)
        if (pid <= 0) {
            Toast.makeText(this, "Άκυρο patientId", Toast.LENGTH_LONG).show()
            finish(); return
        }
        this.patientId = pid

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        btnSave = findViewById(R.id.btnSaveAll)

        pagerAdapter = PatientHistoryPagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 6

        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            tab.text = tabTitles[pos]
        }.attach()

        // Prefill αν υπάρχει ήδη ιστορικό
        lifecycleScope.launch {
            val db = PodologiaDatabase.getDatabase(this@PatientHistoryTabsActivity)
            db.patientHistoryDao()
                .observeByPatientId(patientId)
                .collectLatest { history: PatientHistory? ->
                    existingHistory = history
                    prefillAllTabs(existingHistory)
                }
        }

        btnSave.setOnClickListener { saveAll() }
    }

    private fun saveAll() {
        val aggregator = PatientHistoryAggregator(patientId, existingHistory)

        // Συλλογή δεδομένων από όλα τα fragments μέσω του interface
        supportFragmentManager.fragments.forEach { f ->
            (f as? HistorySection)?.collectInto(aggregator)
        }

        val toSave = aggregator.build()

        lifecycleScope.launch(Dispatchers.IO) {
            val db = PodologiaDatabase.getDatabase(this@PatientHistoryTabsActivity)
            db.patientHistoryDao().upsert(toSave)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@PatientHistoryTabsActivity,
                    "Αποθηκεύτηκε το Αναμνηστικό",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun prefillAllTabs(existing: PatientHistory?) {
        viewPager.post {
            supportFragmentManager.fragments.forEach { f ->
                (f as? HistorySection)?.prefill(existing)
            }
        }
    }
}
