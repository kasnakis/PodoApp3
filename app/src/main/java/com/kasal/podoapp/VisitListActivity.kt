package com.kasal.podoapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PodologiaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VisitListActivity : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var patientId: Int = 0
    private lateinit var list: ListView
    private val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    // A) Προσθήκη λίστας για τα visits
    private var currentVisits: List<com.kasal.podoapp.data.Visit> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_list)

        patientId = intent.getIntExtra("patientId", 0)
        if (patientId == 0) {
            Toast.makeText(this, "Δεν βρέθηκε πελάτης", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        list = findViewById(R.id.listViewVisits)
        val db = PodologiaDatabase.getDatabase(this)

        scope.launch {
            db.visitDao().forPatient(patientId).collectLatest { visits ->
                // B) Αποθήκευση λίστας και δημιουργία adapter
                currentVisits = visits // η πλήρης λίστα Visit από Room

                val items = currentVisits.map { v ->
                    val whenStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("el")).format(Date(v.dateTime))
                    val charge = v.charge ?: "-"
                    "• $whenStr  —  Χρέωση: $charge"
                }

                list.adapter = ArrayAdapter(
                    this@VisitListActivity,
                    android.R.layout.simple_list_item_1,
                    items
                )

                list.setOnItemClickListener { _, _, position, _ ->
                    val selected = currentVisits.getOrNull(position) ?: return@setOnItemClickListener
                    startActivity(
                        Intent(this@VisitListActivity, VisitDetailActivity::class.java)
                            .putExtra("visitId", selected.id)
                    )
                }
            }
        }
    }

    // Γ) Cleanup
    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}