package com.kasal.podoapp.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Appointment
import com.kasal.podoapp.data.PodologiaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class EditAppointmentActivity : AppCompatActivity() {

    private var appointmentId: Int = 0
    private var patientId: Int = 0

    // Views
    private lateinit var tvPatientHeader: TextView
    private lateinit var btnPickDate: Button
    private lateinit var btnPickTime: Button
    private lateinit var etDuration: EditText
    private lateinit var etNotes: EditText
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button

    private val cal = Calendar.getInstance()
    private val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

    private var existing: Appointment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_appointment)

        appointmentId = intent.getIntExtra("appointmentId", 0)
        patientId = intent.getIntExtra("patientId", 0)
        if (appointmentId <= 0) {
            Toast.makeText(this, "Άκυρο ραντεβού", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        bindViews()
        wirePickers()
        title = "Επεξεργασία Ραντεβού"

        val db = PodologiaDatabase.getInstance(this)

        lifecycleScope.launch(Dispatchers.IO) {
            existing = db.appointmentDao().getById(appointmentId)
            val patient = if (patientId > 0) db.patientDao().getPatientById(patientId) else null

            withContext(Dispatchers.Main) {
                tvPatientHeader.text = patient?.fullName ?: "Πελάτης #$patientId"
                existing?.let { populate(it) } ?: run {
                    Toast.makeText(this@EditAppointmentActivity, "Το ραντεβού δεν βρέθηκε", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }

        btnSave.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val updated = collect() ?: return@launch
                db.appointmentDao().upsert(updated)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditAppointmentActivity, "Αποθηκεύτηκε", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        btnDelete.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                existing?.let { db.appointmentDao().deleteById(it.id) }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditAppointmentActivity, "Διαγράφηκε", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun bindViews() {
        tvPatientHeader = findViewById(R.id.tvPatientHeader)
        btnPickDate = findViewById(R.id.btnPickDate)
        btnPickTime = findViewById(R.id.btnPickTime)
        etDuration = findViewById(R.id.etDuration)
        etNotes = findViewById(R.id.etNotes)
        btnSave = findViewById(R.id.btnSave)
        btnDelete = findViewById(R.id.btnDelete)
    }

    private fun wirePickers() {
        btnPickDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    cal.set(Calendar.YEAR, y)
                    cal.set(Calendar.MONTH, m)
                    cal.set(Calendar.DAY_OF_MONTH, d)
                    refreshButtons()
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnPickTime.setOnClickListener {
            TimePickerDialog(
                this,
                { _, h, min ->
                    cal.set(Calendar.HOUR_OF_DAY, h)
                    cal.set(Calendar.MINUTE, min)
                    cal.set(Calendar.SECOND, 0)
                    refreshButtons()
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun refreshButtons() {
        btnPickDate.text = dateFmt.format(cal.time)
        btnPickTime.text = timeFmt.format(cal.time)
    }

    private fun populate(a: Appointment) {
        // TODO: ΒΑΛΕ τα σωστά ΟΝΟΜΑΤΑ ΠΕΔΙΩΝ από το entity Appointment
        // Παραδείγματα: startMillis OR startTimeMillis OR timeMillis
        val start = /* a.startMillis OR a.startTimeMillis OR a.timeMillis */ 0L
        // Παραδείγματα: durationMinutes OR durationMin OR duration
        val duration = /* a.durationMinutes OR a.durationMin OR a.duration */ 30
        // Παραδείγματα: notes OR comment
        val notes = /* a.notes OR a.comment */ null

        cal.timeInMillis = start
        refreshButtons()
        etDuration.setText(duration.toString())
        etNotes.setText(notes ?: "")
    }

    private fun collect(): Appointment? {
        val base = existing ?: return null
        val duration = etDuration.text.toString().toIntOrNull() ?: 30
        val notes = etNotes.text.toString().trim().ifEmpty { null }
        val whenMillis = cal.timeInMillis

        // TODO: Ενημέρωσε τα ΟΝΟΜΑΤΑ πεδίων στο copy(...) ώστε να ταιριάζουν 1:1 με το Appointment
        return base.copy(
            /* startMillis = */ whenMillis,
            /* durationMinutes = */ duration,
            /* notes = */ notes
        )
    }
}
