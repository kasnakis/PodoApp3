package com.kasal.podoapp.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Appointment
import com.kasal.podoapp.data.PodologiaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AppointmentCalendarActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var adapter: AppointmentAdapter
    private lateinit var textSelectedDate: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonPickDate: Button

    private var selectedDayUtcMillis: Long = todayUtcStartMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_calendar)

        textSelectedDate = findViewById(R.id.textSelectedDate)
        recyclerView = findViewById(R.id.recyclerViewDayAppointments)
        buttonPickDate = findViewById(R.id.buttonPickDate)

        adapter = AppointmentAdapter(
            onCompleted = { appt -> markCompleted(appt) },
            onEdit = { appt -> openEdit(appt) },
            onDelete = { appt -> confirmDelete(appt) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        textSelectedDate.text = formatDateForDisplay(selectedDayUtcMillis)
        loadAppointmentsForDay(selectedDayUtcMillis)

        buttonPickDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Επιλογή Ημερομηνίας")
                .setSelection(selectedDayUtcMillis)
                .build()

            picker.show(supportFragmentManager, "date_picker")
            picker.addOnPositiveButtonClickListener { selectionUtc ->
                selectedDayUtcMillis = startOfUtcDay(selectionUtc)
                textSelectedDate.text = formatDateForDisplay(selectedDayUtcMillis)
                loadAppointmentsForDay(selectedDayUtcMillis)
            }
        }
    }

    private fun markCompleted(appt: Appointment) {
        scope.launch(Dispatchers.IO) {
            val db = PodologiaDatabase.getDatabase(this@AppointmentCalendarActivity)
            db.appointmentDao().updateStatus(appt.id, "COMPLETED")
            withContext(Dispatchers.Main) { loadAppointmentsForDay(selectedDayUtcMillis) }
        }
    }

    private fun openEdit(appt: Appointment) {
        startActivity(
            Intent(this, EditAppointmentActivity::class.java)
                .putExtra("appointmentId", appt.id)
        )
    }

    private fun confirmDelete(appt: Appointment) {
        AlertDialog.Builder(this)
            .setTitle("Διαγραφή ραντεβού")
            .setMessage("Σίγουρα θέλεις να διαγράψεις αυτό το ραντεβού;")
            .setPositiveButton("Ναι") { _, _ -> deleteAppointment(appt) }
            .setNegativeButton("Όχι", null)
            .show()
    }

    private fun deleteAppointment(appt: Appointment) {
        scope.launch(Dispatchers.IO) {
            val db = PodologiaDatabase.getDatabase(this@AppointmentCalendarActivity)
            db.appointmentDao().delete(appt)
            withContext(Dispatchers.Main) { loadAppointmentsForDay(selectedDayUtcMillis) }
        }
    }

    private fun loadAppointmentsForDay(dayUtcStart: Long) {
        val (start, end) = dayBoundsUtc(dayUtcStart)
        val db = PodologiaDatabase.getDatabase(this)

        scope.launch(Dispatchers.IO) {
            val list = db.appointmentDao().getAppointmentsForDate(start, end)
            withContext(Dispatchers.Main) { adapter.submit(list) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun todayUtcStartMillis(): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun dayBoundsUtc(dayStartUtc: Long): Pair<Long, Long> {
        val start = dayStartUtc
        val end = dayStartUtc + (24L * 60L * 60L * 1000L) - 1L
        return start to end
    }

    private fun startOfUtcDay(anyUtcMillis: Long): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = anyUtcMillis }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun formatDateForDisplay(dayStartUtc: Long): String {
        val local = Date(dayStartUtc)
        val fmt = SimpleDateFormat("EEEE dd/MM/yyyy", Locale("el"))
        return fmt.format(local)
    }
}
