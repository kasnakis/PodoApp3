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
    private lateinit var tvDelete: TextView

    private val cal = Calendar.getInstance()
    private val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

    private var existing: Appointment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_appointment)

        appointmentId = intent.getIntExtra("appointmentId", 0)
        patientId = intent.getIntExtra("patientId", 0)

        if (appointmentId <= 0 && patientId <= 0) {
            Toast.makeText(this, "Invalid request", Toast.LENGTH_SHORT).show()
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

        tvDelete.setOnClickListener {
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
        tvDelete = findViewById(R.id.tvDelete)
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
        val start = a.dateTime
        val notes = a.notes
        // The Appointment data class doesn't have a 'duration' field, so this line is removed.

        cal.timeInMillis = start
        refreshButtons()
        etNotes.setText(notes ?: "")
    }

    private fun collect(): Appointment? {
        val base = existing ?: return null
        val notes = etNotes.text.toString().trim().ifEmpty { null }
        val whenMillis = cal.timeInMillis

        // The Appointment data class doesn't have a 'duration' field, so this parameter is removed.
        return base.copy(
            dateTime = whenMillis,
            notes = notes
        )
    }
}