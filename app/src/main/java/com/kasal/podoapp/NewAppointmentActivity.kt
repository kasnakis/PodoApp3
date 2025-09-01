package com.kasal.podoapp.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Appointment
import com.kasal.podoapp.data.PodologiaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NewAppointmentActivity : AppCompatActivity() {

    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var editTextType: EditText
    private lateinit var editTextNotes: EditText
    private lateinit var buttonSave: Button

    private val calendar = Calendar.getInstance()
    private var patientId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_appointment)

        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)
        editTextType = findViewById(R.id.editTextType)
        editTextNotes = findViewById(R.id.editTextNotes)
        buttonSave = findViewById(R.id.buttonSaveAppointment)

        patientId = intent.getIntExtra("patientId", 0)

        editTextDate.setOnClickListener { showDatePicker() }
        editTextTime.setOnClickListener { showTimePicker() }

        buttonSave.setOnClickListener { saveAppointment() }
    }

    private fun showDatePicker() {
        val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
            calendar.set(Calendar.YEAR, y)
            calendar.set(Calendar.MONTH, m)
            calendar.set(Calendar.DAY_OF_MONTH, d)
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            editTextDate.setText(formatter.format(calendar.time))
        }

        DatePickerDialog(
            this, listener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val listener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            editTextTime.setText(formatter.format(calendar.time))
        }

        TimePickerDialog(
            this, listener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun saveAppointment() {
        val date = editTextDate.text.toString()
        val time = editTextTime.text.toString()
        val type = editTextType.text.toString()
        val notes = editTextNotes.text.toString()

        if (date.isBlank() || time.isBlank()) {
            Toast.makeText(this, "Συμπληρώστε ημερομηνία και ώρα", Toast.LENGTH_SHORT).show()
            return
        }

        val dateTimeMillis = try {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            parser.parse("$date $time")!!.time
        } catch (e: Exception) {
            Toast.makeText(this, "Μη έγκυρη ημερομηνία/ώρα", Toast.LENGTH_SHORT).show()
            return
        }

        val appointment = Appointment(
            patientId = patientId,
            dateTime = dateTimeMillis,
            status = if (type.isBlank()) "SCHEDULED" else type,
            notes = if (notes.isBlank()) null else notes,
            charge = null,
            treatment = null
        )

        CoroutineScope(Dispatchers.IO).launch {
            val db = PodologiaDatabase.getDatabase(this@NewAppointmentActivity)
            db.appointmentDao().insert(appointment)
            runOnUiThread {
                Toast.makeText(this@NewAppointmentActivity, "Το ραντεβού καταχωρήθηκε", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
