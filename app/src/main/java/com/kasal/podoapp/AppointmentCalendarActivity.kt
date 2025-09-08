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
import com.kasal.podoapp.ui.PatientDetailActivity

class AppointmentCalendarActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var adapter: AppointmentForDayAdapter
    private lateinit var textSelectedDate: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonPickDate: Button

    // Κρατάμε την επιλεγμένη μέρα σε UTC 00:00 (millis)
    private var selectedDayUtcMillis: Long = todayUtcStartMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_calendar)

        textSelectedDate = findViewById(R.id.textSelectedDate)
        recyclerView = findViewById(R.id.recyclerViewDayAppointments)
        buttonPickDate = findViewById(R.id.buttonPickDate)

        adapter = AppointmentForDayAdapter(
            onItemClick = { appt -> openEditAppointment(appt) },
            onItemLongClick = { appt -> openPatientProfile(appt.patientId) }
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

    private fun openEditAppointment(appt: Appointment) {
        // ✅ Περνάμε ΜΟΝΟ το ID για να μη χρειάζεται Serializable/Parcelable
        val intent = Intent(this, EditAppointmentActivity::class.java)
        intent.putExtra("appointmentId", appt.id)
        startActivity(intent)
    }

    private fun openPatientProfile(patientId: Int) {
        val i = Intent(this, PatientDetailActivity::class.java)
        i.putExtra("patientId", patientId)
        startActivity(i)
    }

    private fun loadAppointmentsForDay(dayUtcStart: Long) {
        val (start, end) = dayBoundsUtc(dayUtcStart)
        val db = PodologiaDatabase.getDatabase(this)

        scope.launch(Dispatchers.IO) {
            val appointments = db.appointmentDao().getAppointmentsForDate(start, end)

            // Συμπλήρωσε με ονοματεπώνυμο (fullName) για εμφάνιση
            val patientDao = db.patientDao()
            val items: List<Pair<Appointment, String>> = appointments.map { a ->
                val p = patientDao.getPatientById(a.patientId)
                val name = p?.fullName ?: "Πελάτης #${a.patientId}"
                a to name
            }

            withContext(Dispatchers.Main) {
                adapter.submitList(items)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    /** UTC 00:00 για σήμερα */
    private fun todayUtcStartMillis(): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /** [startOfDayUtc, endOfDayUtcInclusive] για ένα UTC start-of-day */
    private fun dayBoundsUtc(dayStartUtc: Long): Pair<Long, Long> {
        val start = dayStartUtc
        val end = dayStartUtc + (24L * 60L * 60L * 1000L) - 1L
        return start to end
    }

    /** Γυρνά οποιοδήποτε UTC millis στο UTC 00:00 της ημέρας του */
    private fun startOfUtcDay(anyUtcMillis: Long): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = anyUtcMillis }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /** Μορφοποίηση για εμφάνιση (ελληνικά) */
    private fun formatDateForDisplay(dayStartUtc: Long): String {
        val local = Date(dayStartUtc)
        val fmt = SimpleDateFormat("EEEE dd/MM/yyyy", Locale("el"))
        return fmt.format(local)
    }
}
