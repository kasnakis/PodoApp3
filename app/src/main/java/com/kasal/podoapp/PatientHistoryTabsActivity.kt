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
import com.kasal.podoapp.data.Patient
import com.kasal.podoapp.data.PatientHistory
import com.kasal.podoapp.data.PodologiaDatabase
import kotlinx.coroutines.Dispatchers
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

        // ΝΕΟ (safe):
        val patientId = intent.getIntExtra("patientId", -1)
        if (patientId <= 0) {
            Toast.makeText(this, "Άκυρο patientId", Toast.LENGTH_LONG).show()
            finish(); return
        }
        this.patientId = patientId


        // Views
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        btnSave = findViewById(R.id.btnSaveAll)

        // Adapter για τα tabs
        pagerAdapter = PatientHistoryPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        // Tab titles
        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            tab.text = tabTitles[pos]
        }.attach()

        // Prefill αν υπάρχει ήδη ιστορικό
        lifecycleScope.launch {
            val db = PodologiaDatabase.getDatabase(this@PatientHistoryTabsActivity)
            val history = withContext(Dispatchers.IO) {
                db.patientHistoryDao().getByPatientId(patientId)
            }
            existingHistory = history
            pagerAdapter.prefillAll(existingHistory)
        }

        // Αποθήκευση όλων των tabs
        btnSave.setOnClickListener { saveAll() }
    }

    private fun saveAll() {
        // Συλλογή όλων των πεδίων από τα fragments
        val aggregator = PatientHistoryAggregator(patientId, existingHistory)
        pagerAdapter.collectAllInto(aggregator)
        val toSave = aggregator.build()

        // Upsert στη βάση
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
}