package com.kasal.podoapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PodologiaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AppointmentCalendarActivity : AppCompatActivity() {

    private lateinit var adapter: AppointmentAdapter // χρησιμοποίησε τον δικό σου adapter
    private lateinit var textSelectedDate: TextView
    private var selectedDate: String = getTodayDate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_calendar)

        textSelectedDate = findViewById(R.id.textSelectedDate)
        val buttonPickDate = findViewById<Button>(R.id.buttonPickDate)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDayAppointments)

        adapter = AppointmentAdapter { /* actions αν έχεις */ }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        textSelectedDate.text = formatDateForDisplay(selectedDate)
        loadAppointmentsWithNames(selectedDate)

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
                loadAppointmentsWithNames(date)
            }
        }
    }

    private fun loadAppointmentsWithNames(date: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = PodologiaDatabase.getDatabase(this@AppointmentCalendarActivity)
            val appointments = db.appointmentDao().getAppointmentsForDate(date).first()
            val listWithNames = appointments.map { a ->
                val p = db.patientDao().getPatientById(a.patientId)
                a to (p?.fullName ?: "(χωρίς όνομα)")
            }
            withContext(Dispatchers.Main) { /* adapter.submitList(listWithNames) */ }
        }
    }

    private fun getTodayDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun formatDateForDisplay(date: String): String {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val output = SimpleDateFormat("EEEE dd/MM/yyyy", Locale("el"))
        return try { output.format(input.parse(date)!!) } catch (_: Exception) { date }
    }
}
