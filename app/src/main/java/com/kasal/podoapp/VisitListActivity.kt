package com.kasal.podoapp.ui

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
                val items = visits.map { v ->
                    val whenStr = fmt.format(Date(v.dateTime))
                    "$whenStr • ${v.treatment ?: "Θεραπεία -"} • ${v.charge ?: "Χρέωση -"}"
                }
                list.adapter = ArrayAdapter(
                    this@VisitListActivity,
                    android.R.layout.simple_list_item_1,
                    items
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
