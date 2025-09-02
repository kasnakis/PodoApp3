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

    private var selectedDayUtcMillis: Long = todayUtcStartMillis()

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
            onCompleted = { appt -> convertToVisit(appt) },  // «Ολοκλήρωση» = μετατροπή σε Επίσκεψη
            onEdit = { appt -> openEdit(appt) },
            onDelete = { appt -> confirmDelete(appt) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        spinnerSort.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Ώρα ↑", "Ώρα ↓", "Κατάσταση A-Z", "Κατάσταση Z-A")
        )

        textSelectedDate.text = fmtDisplay.format(Date(selectedDayUtcMillis))
        loadAppointmentsForDay(selectedDayUtcMillis)

        buttonPickDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Επιλογή Ημερομηνίας")
                .setSelection(selectedDayUtcMillis)
                .build()

            picker.show(supportFragmentManager, "date_picker")
            picker.addOnPositiveButtonClickListener { selectionUtc ->
                selectedDayUtcMillis = startOfUtcDay(selectionUtc)
                textSelectedDate.text = fmtDisplay.format(Date(selectedDayUtcMillis))
                loadAppointmentsForDay(selectedDayUtcMillis)
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
            // Μαρκάρουμε το ραντεβού
            db.appointmentDao().updateStatus(appt.id, "COMPLETED")
            // Δημιουργούμε την επίσκεψη
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
                // Άνοιγμα VisitDetail
                startActivity(
                    Intent(this@AppointmentCalendarActivity, VisitDetailActivity::class.java)
                        .putExtra("visitId", newId.toInt())
                )
                // Ανανέωση λίστας ημέρας
                loadAppointmentsForDay(selectedDayUtcMillis)
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
            withContext(Dispatchers.Main) { loadAppointmentsForDay(selectedDayUtcMillis) }
        }
    }

    private fun loadAppointmentsForDay(dayUtcStart: Long) {
        val (start, end) = dayBoundsUtc(dayUtcStart)
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
            0 -> list = list.sortedBy { it.dateTime }                  // ώρα ↑
            1 -> list = list.sortedByDescending { it.dateTime }        // ώρα ↓
            2 -> list = list.sortedBy { it.status }                    // status A-Z
            3 -> list = list.sortedByDescending { it.status }          // status Z-A
        }

        filteredAppointments = list
        adapter.submit(filteredAppointments)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    // Helpers
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
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
