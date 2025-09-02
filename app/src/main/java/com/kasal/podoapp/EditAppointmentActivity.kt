package com.kasal.podoapp.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Appointment
import com.kasal.podoapp.data.PodologiaDatabase
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class EditAppointmentActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var textWhen: TextView
    private lateinit var buttonPickDate: Button
    private lateinit var buttonPickTime: Button
    private lateinit var spinnerStatus: Spinner
    private lateinit var editTreatment: EditText
    private lateinit var editCharge: EditText
    private lateinit var editNotes: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button

    private var appointment: Appointment? = null
    private var selectedDateTime: Long = System.currentTimeMillis()

    private val statuses = listOf("PENDING", "CONFIRMED", "COMPLETED", "CANCELLED")
    private val fmtFull = SimpleDateFormat("EEEE dd/MM/yyyy, HH:mm", Locale("el"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_appointment)

        textWhen = findViewById(R.id.textApptWhen)
        buttonPickDate = findViewById(R.id.buttonPickDate)
        buttonPickTime = findViewById(R.id.buttonPickTime)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        editTreatment = findViewById(R.id.editTreatment)
        editCharge = findViewById(R.id.editCharge)
        editNotes = findViewById(R.id.editNotes)
        buttonSave = findViewById(R.id.buttonSaveAppointment)
        buttonDelete = findViewById(R.id.buttonDeleteAppointment)

        spinnerStatus.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses)

        val apptId = intent.getIntExtra("appointmentId", -1)
        if (apptId <= 0) {
            Toast.makeText(this, "Άκυρο appointmentId", Toast.LENGTH_LONG).show()
            finish(); return
        }

        val db = PodologiaDatabase.getDatabase(this)
        scope.launch(Dispatchers.IO) {
            appointment = db.appointmentDao().getById(apptId)
            withContext(Dispatchers.Main) {
                if (appointment == null) {
                    Toast.makeText(this@EditAppointmentActivity, "Δεν βρέθηκε ραντεβού", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    bindUi(appointment!!)
                }
            }
        }

        buttonPickDate.setOnClickListener { showDatePicker() }
        buttonPickTime.setOnClickListener { showTimePicker() }
        buttonSave.setOnClickListener { saveChanges() }
        buttonDelete.setOnClickListener { confirmDelete() }
    }

    private fun bindUi(a: Appointment) {
        selectedDateTime = a.dateTime
        textWhen.text = fmtFull.format(Date(selectedDateTime))
        editTreatment.setText(a.treatment ?: "")
        editCharge.setText(a.charge ?: "")
        editNotes.setText(a.notes ?: "")

        val idx = statuses.indexOf(a.status).takeIf { it >= 0 } ?: 0
        spinnerStatus.setSelection(idx)
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance().apply { timeInMillis = selectedDateTime }
        DatePickerDialog(
            this,
            { _, y, m, d ->
                cal.set(Calendar.YEAR, y)
                cal.set(Calendar.MONTH, m)
                cal.set(Calendar.DAY_OF_MONTH, d)
                selectedDateTime = cal.timeInMillis
                textWhen.text = fmtFull.format(Date(selectedDateTime))
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val cal = Calendar.getInstance().apply { timeInMillis = selectedDateTime }
        TimePickerDialog(
            this,
            { _, h, min ->
                cal.set(Calendar.HOUR_OF_DAY, h)
                cal.set(Calendar.MINUTE, min)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                selectedDateTime = cal.timeInMillis
                textWhen.text = fmtFull.format(Date(selectedDateTime))
            },
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
        ).show()
    }

    private fun saveChanges() {
        val a = appointment ?: return
        val updated = a.copy(
            dateTime = selectedDateTime,
            status = spinnerStatus.selectedItem?.toString() ?: "PENDING",
            treatment = editTreatment.text?.toString(),
            charge = editCharge.text?.toString(),
            notes = editNotes.text?.toString()
        )
        scope.launch(Dispatchers.IO) {
            PodologiaDatabase.getDatabase(this@EditAppointmentActivity).appointmentDao().update(updated)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@EditAppointmentActivity, "Αποθηκεύτηκε", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Διαγραφή ραντεβού")
            .setMessage("Σίγουρα θέλεις να διαγράψεις αυτό το ραντεβού;")
            .setPositiveButton("Ναι") { _, _ -> deleteAppointment() }
            .setNegativeButton("Όχι", null)
            .show()
    }

    private fun deleteAppointment() {
        val a = appointment ?: return
        scope.launch(Dispatchers.IO) {
            PodologiaDatabase.getDatabase(this@EditAppointmentActivity).appointmentDao().delete(a)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@EditAppointmentActivity, "Διαγράφηκε", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
