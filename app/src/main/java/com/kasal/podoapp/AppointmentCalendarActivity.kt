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
import com.kasal.podoapp.data.Appointment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AppointmentCalendarActivity : AppCompatActivity() {

    private lateinit var adapter: AppointmentForDayAdapter
    private lateinit var textSelectedDate: TextView
    private var selectedDate: String = getTodayDate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_calendar)

        textSelectedDate = findViewById(R.id.textSelectedDate)
        val buttonPickDate = findViewById<Button>(R.id.buttonPickDate)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDayAppointments)

        adapter = AppointmentForDayAdapter()
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

    private fun dayBoundsMillis(date: String): Pair<Long, Long> {
        val parts = date.split("-")
        val y = parts.getOrNull(0)?.toIntOrNull() ?: 1970
        val m0 = (parts.getOrNull(1)?.toIntOrNull() ?: 1) - 1
        val d = parts.getOrNull(2)?.toIntOrNull() ?: 1
        val start = Calendar.getInstance().apply { set(y, m0, d, 0, 0, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
        val end = Calendar.getInstance().apply { set(y, m0, d, 23, 59, 59); set(Calendar.MILLISECOND, 999) }.timeInMillis
        return start to end
    }

    private fun loadAppointmentsWithNames(date: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = PodologiaDatabase.getDatabase(this@AppointmentCalendarActivity)
            val (start, end) = dayBoundsMillis(date)
            val appointments = db.appointmentDao().getAppointmentsForDate(start, end).first()
            val listWithNames: List<Pair<Appointment, String>> = appointments.map { appointment ->
                val patient = db.patientDao().getPatientById(appointment.patientId)
                appointment to (patient?.fullName ?: "(χωρίς όνομα)")
            }

            withContext(Dispatchers.Main) {
                adapter.submitList(listWithNames)
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
        } catch (_: Exception) {
            date
        }
    }
}
