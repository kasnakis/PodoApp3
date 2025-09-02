package com.kasal.podoapp.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Appointment
import com.kasal.podoapp.data.PodologiaDatabase
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class EditAppointmentActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var textDate: TextView
    private lateinit var textTime: TextView
    private lateinit var btnPickDate: Button
    private lateinit var btnPickTime: Button
    private lateinit var spinnerStatus: Spinner
    private lateinit var editNotes: EditText
    private lateinit var editTreatment: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var appt: Appointment? = null
    private var workingDateTimeMillis: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_appointment)

        textDate = findViewById(R.id.textDate)
        textTime = findViewById(R.id.textTime)
        btnPickDate = findViewById(R.id.buttonPickDate)
        btnPickTime = findViewById(R.id.buttonPickTime)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        editNotes = findViewById(R.id.editNotes)
        editTreatment = findViewById(R.id.editTreatment)
        btnSave = findViewById(R.id.buttonSave)
        btnCancel = findViewById(R.id.buttonCancel)

        // statuses
        val statuses = listOf("SCHEDULED", "COMPLETED", "CANCELED")
        val adapterStatuses: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses)
        spinnerStatus.adapter = adapterStatuses

        val id = intent.getIntExtra("appointmentId", -1)
        if (id <= 0) {
            Toast.makeText(this, "Άκυρο ραντεβού", Toast.LENGTH_LONG).show()
            finish(); return
        }

        val db = PodologiaDatabase.getDatabase(this)
        scope.launch(Dispatchers.IO) {
            appt = db.appointmentDao().getById(id)
            withContext(Dispatchers.Main) {
                if (appt == null) {
                    Toast.makeText(this@EditAppointmentActivity, "Δεν βρέθηκε ραντεβού", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    bindUi(appt!!, adapterStatuses)
                }
            }
        }

        btnPickDate.setOnClickListener { openDatePicker() }
        btnPickTime.setOnClickListener { openTimePicker() }

        btnSave.setOnClickListener { saveChanges() }
        btnCancel.setOnClickListener { finish() }
    }

    private fun bindUi(a: Appointment, adapterStatuses: ArrayAdapter<String>) {
        workingDateTimeMillis = a.dateTime
        textDate.text = formatDate(workingDateTimeMillis)
        textTime.text = formatTime(workingDateTimeMillis)
        editNotes.setText(a.notes ?: "")
        editTreatment.setText(a.treatment ?: "")

        // set spinner to current status
        val idx = adapterStatuses.getPosition(a.status ?: "SCHEDULED")
        spinnerStatus.setSelection(if (idx >= 0) idx else 0)
    }

    private fun openDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Επιλογή Ημερομηνίας")
            .setSelection(workingDateTimeMillis)
            .build()
        picker.show(supportFragmentManager, "date_picker")
        picker.addOnPositiveButtonClickListener { selectionUtc ->
            // Διατήρησε ώρα, άλλαξε μόνο ημερομηνία
            val calSel = Calendar.getInstance().apply { timeInMillis = selectionUtc }
            val cal = Calendar.getInstance().apply { timeInMillis = workingDateTimeMillis }
            cal.set(Calendar.YEAR, calSel.get(Calendar.YEAR))
            cal.set(Calendar.MONTH, calSel.get(Calendar.MONTH))
            cal.set(Calendar.DAY_OF_MONTH, calSel.get(Calendar.DAY_OF_MONTH))
            workingDateTimeMillis = cal.timeInMillis
            textDate.text = formatDate(workingDateTimeMillis)
        }
    }

    private fun openTimePicker() {
        val cal = Calendar.getInstance().apply { timeInMillis = workingDateTimeMillis }
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(cal.get(Calendar.HOUR_OF_DAY))
            .setMinute(cal.get(Calendar.MINUTE))
            .setTitleText("Επιλογή Ώρας")
            .build()
        picker.show(supportFragmentManager, "time_picker")
        picker.addOnPositiveButtonClickListener {
            cal.set(Calendar.HOUR_OF_DAY, picker.hour)
            cal.set(Calendar.MINUTE, picker.minute)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            workingDateTimeMillis = cal.timeInMillis
            textTime.text = formatTime(workingDateTimeMillis)
        }
    }

    private fun saveChanges() {
        val a = appt ?: return
        val newStatus = spinnerStatus.selectedItem?.toString() ?: "SCHEDULED"
        val newNotes = editNotes.text?.toString()
        val newTreatment = editTreatment.text?.toString()

        val updated = a.copy(
            dateTime = workingDateTimeMillis,
            status = newStatus,
            notes = newNotes,
            treatment = newTreatment
        )

        val db = PodologiaDatabase.getDatabase(this)
        scope.launch(Dispatchers.IO) {
            db.appointmentDao().update(updated)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@EditAppointmentActivity, "Αποθηκεύτηκε", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    private fun formatDate(millis: Long): String =
        SimpleDateFormat("EEEE dd/MM/yyyy", Locale("el")).format(Date(millis))

    private fun formatTime(millis: Long): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(millis))

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
