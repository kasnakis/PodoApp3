package com.kasal.podoapp.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Appointment
import com.kasal.podoapp.data.PodologiaDatabase
import com.kasal.podoapp.data.Visit
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class AppointmentCalendarActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var adapter: AppointmentAdapter
    private lateinit var textSelectedDate: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonPickDate: Button
    private lateinit var editSearch: EditText
    private lateinit var spinnerSort: Spinner

    // Κρατάμε ΤΟΠΙΚΟ start-of-day (όχι UTC)
    private var selectedDayLocalStartMillis: Long = localTodayStartMillis()

    private var currentAppointments: List<Appointment> = emptyList()
    private var filteredAppointments: List<Appointment> = emptyList()

    private val fmtDisplay = SimpleDateFormat("EEEE dd/MM/yyyy", Locale("el"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_calendar)

        textSelectedDate = findViewById(R.id.textSelectedDate)
        recyclerView = findViewById(R.id.recyclerViewDayAppointments)
        buttonPickDate = findViewById(R.id.buttonPickDate)
        editSearch = findViewById(R.id.editSearchAppt)
        spinnerSort = findViewById(R.id.spinnerSortAppt)

        adapter = AppointmentAdapter(
            onCompleted = { appt -> convertToVisit(appt) },
            onEdit = { appt -> openEdit(appt) },
            onDelete = { appt -> confirmDelete(appt) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        spinnerSort.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Ώρα ↑", "Ώρα ↓", "Κατάσταση A-Z", "Κατάσταση Z-A")
        )

        textSelectedDate.text = fmtDisplay.format(Date(selectedDayLocalStartMillis))
        loadAppointmentsForDay(selectedDayLocalStartMillis)

        buttonPickDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Επιλογή Ημερομηνίας")
                // Δείχνουμε την τρέχουσα τοπική μέρα ως επιλογή (μετατρέπουμε σε UTC ms τοπικής ημέρας)
                .setSelection(localStartToUtc(selectedDayLocalStartMillis))
                .build()

            picker.show(supportFragmentManager, "date_picker")
            picker.addOnPositiveButtonClickListener { selectionUtc ->
                // Το MaterialDatePicker δίνει UTC 00:00. Μετατρέπουμε σε ΤΟΠΙΚΟ 00:00 της ίδιας ημερομηνίας.
                selectedDayLocalStartMillis = utcMidnightToLocalStart(selectionUtc)
                textSelectedDate.text = fmtDisplay.format(Date(selectedDayLocalStartMillis))
                loadAppointmentsForDay(selectedDayLocalStartMillis)
            }
        }

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { applyFilters() }
        })
        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: android.view.View?, pos: Int, id: Long) { applyFilters() }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun convertToVisit(appt: Appointment) {
        scope.launch(Dispatchers.IO) {
            val db = PodologiaDatabase.getDatabase(this@AppointmentCalendarActivity)
            db.appointmentDao().updateStatus(appt.id, "COMPLETED")
            val newId = db.visitDao().insert(
                Visit(
                    patientId = appt.patientId,
                    appointmentId = appt.id,
                    dateTime = System.currentTimeMillis(),
                    notes = appt.notes,
                    charge = appt.charge,
                    treatment = appt.treatment
                )
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AppointmentCalendarActivity, "Δημιουργήθηκε επίσκεψη", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@AppointmentCalendarActivity, VisitDetailActivity::class.java)
                    .putExtra("visitId", newId.toInt()))
                loadAppointmentsForDay(selectedDayLocalStartMillis)
            }
        }
    }

    private fun openEdit(appt: Appointment) {
        startActivity(Intent(this, EditAppointmentActivity::class.java).putExtra("appointmentId", appt.id))
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
            PodologiaDatabase.getDatabase(this@AppointmentCalendarActivity).appointmentDao().delete(appt)
            withContext(Dispatchers.Main) { loadAppointmentsForDay(selectedDayLocalStartMillis) }
        }
    }

    private fun loadAppointmentsForDay(dayLocalStart: Long) {
        val (start, end) = localDayBounds(dayLocalStart)
        scope.launch(Dispatchers.IO) {
            val list = PodologiaDatabase.getDatabase(this@AppointmentCalendarActivity)
                .appointmentDao().getAppointmentsForDate(start, end)
            withContext(Dispatchers.Main) {
                currentAppointments = list
                applyFilters()
            }
        }
    }

    private fun applyFilters() {
        val q = editSearch.text?.toString()?.trim()?.lowercase(Locale.getDefault()) ?: ""
        var list = currentAppointments
        if (q.isNotEmpty()) {
            list = list.filter { a ->
                val s = a.status.lowercase(Locale.getDefault())
                val t = a.treatment?.lowercase(Locale.getDefault()) ?: ""
                val n = a.notes?.lowercase(Locale.getDefault()) ?: ""
                val c = a.charge?.lowercase(Locale.getDefault()) ?: ""
                s.contains(q) || t.contains(q) || n.contains(q) || c.contains(q)
            }
        }
        when (spinnerSort.selectedItemPosition) {
            0 -> list = list.sortedBy { it.dateTime }
            1 -> list = list.sortedByDescending { it.dateTime }
            2 -> list = list.sortedBy { it.status }
            3 -> list = list.sortedByDescending { it.status }
        }
        filteredAppointments = list
        adapter.submit(filteredAppointments)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    // ================= Helpers (ΤΟΠΙΚΗ ΖΩΝΗ) =================

    /** Τοπικό 00:00 σήμερα */
    private fun localTodayStartMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /** [start, endInclusive] για τοπική μέρα */
    private fun localDayBounds(localStart: Long): Pair<Long, Long> {
        val start = localStart
        val end = localStart + 24L * 60L * 60L * 1000L - 1L
        return start to end
    }

    /** Μετατροπή: UTC 00:00 από το date-picker -> ΤΟΠΙΚΟ 00:00 της ίδιας ημερομηνίας */
    private fun utcMidnightToLocalStart(utcMidnight: Long): Long {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcMidnight }
        val y = utc.get(Calendar.YEAR)
        val m = utc.get(Calendar.MONTH)
        val d = utc.get(Calendar.DAY_OF_MONTH)
        val local = Calendar.getInstance()
        local.set(y, m, d, 0, 0, 0)
        local.set(Calendar.MILLISECOND, 0)
        return local.timeInMillis
    }

    /** Τοπικό start-of-day -> UTC milllis (για να δείξουμε σωστά default στο date-picker) */
    private fun localStartToUtc(localStart: Long): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = localStart }
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utc.set(y, m, d, 0, 0, 0)
        utc.set(Calendar.MILLISECOND, 0)
        return utc.timeInMillis
    }
}
