package com.kasal.podoapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PodologiaDatabase
import com.kasal.podoapp.data.Visit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class VisitCalendarActivity : AppCompatActivity() {

    private lateinit var adapter: VisitForDayAdapter
    private lateinit var textSelectedDate: TextView
    private var selectedDate: String = getTodayDate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_calendar)

        textSelectedDate = findViewById(R.id.textSelectedDate)
        val buttonPickDate = findViewById<Button>(R.id.buttonPickDate)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDayVisits)

        adapter = VisitForDayAdapter { visit ->
            openPatientProfile(visit)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        textSelectedDate.text = formatDateForDisplay(selectedDate)
        loadVisitsForDate(selectedDate)

        buttonPickDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Επιλογή Ημερομηνίας")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            picker.show(supportFragmentManager, picker.toString())

            picker.addOnPositiveButtonClickListener { selection ->
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selection))
                selectedDate = date
                textSelectedDate.text = formatDateForDisplay(date)
                loadVisitsForDate(date)
            }
        }
    }

    private fun loadVisitsForDate(date: String) {
        CoroutineScope(Dispatchers.IO).launch {
            PodologiaDatabase.getDatabase(this@VisitCalendarActivity)
                .visitDao()
                .getVisitsForDate(date)
                .collect { visits ->
                    withContext(Dispatchers.Main) {
                        adapter.submitList(visits)
                    }
                }
        }
    }

    private fun openPatientProfile(visit: Visit) {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = PodologiaDatabase.getDatabase(this@VisitCalendarActivity).patientDao()
            val patient = dao.getPatientById(visit.patientId)
            withContext(Dispatchers.Main) {
                if (patient != null) {
                    val intent = Intent(this@VisitCalendarActivity, PatientDetailActivity::class.java)
                    intent.putExtra("patient", patient)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@VisitCalendarActivity, "Ο πελάτης δεν βρέθηκε", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun formatDateForDisplay(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE dd/MM/yyyy", Locale("el"))
        return try {
            val parsed = inputFormat.parse(date)
            outputFormat.format(parsed!!)
        } catch (e: Exception) {
            date
        }
    }
}
