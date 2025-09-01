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

class EditAppointmentActivity : AppCompatActivity() {

    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var editTextStatus: EditText
    private lateinit var editTextNotes: EditText
    private lateinit var buttonUpdate: Button

    private val calendar = Calendar.getInstance()
    private lateinit var appointment: Appointment

    private val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_appointment)

        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)
        editTextStatus = findViewById(R.id.editTextStatus)
        editTextNotes = findViewById(R.id.editTextNotes)
        buttonUpdate = findViewById(R.id.buttonUpdateAppointment)

        // Προσοχή: το Appointment πρέπει να είναι Serializable/Parcelable.
        appointment = intent.getSerializableExtra("appointment") as Appointment

        // Fill fields από dateTime (Long)
        val whenDate = Date(appointment.dateTime)
        editTextDate.setText(dateFmt.format(whenDate))
        editTextTime.setText(timeFmt.format(whenDate))
        editTextStatus.setText(appointment.status)
        editTextNotes.setText(appointment.notes ?: "")

        editTextDate.setOnClickListener { showDatePicker() }
        editTextTime.setOnClickListener { showTimePicker() }

        buttonUpdate.setOnClickListener {
            val date = editTextDate.text.toString()
            val time = editTextTime.text.toString()
            val status = editTextStatus.text.toString()
            val notes = editTextNotes.text.toString()

            if (date.isBlank() || time.isBlank() || status.isBlank()) {
                Toast.makeText(this, "Συμπληρώστε ημερομηνία, ώρα και κατάσταση", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Parse σε millis
            val datetimeMillis = try {
                val combined = "$date $time"
                val parser = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                parser.parse(combined)!!.time
            } catch (_: Exception) {
                Toast.makeText(this, "Μη έγκυρη ημερομηνία/ώρα", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updated = appointment.copy(
                dateTime = datetimeMillis,
                status = status,
                notes = if (notes.isBlank()) null else notes
            )

            CoroutineScope(Dispatchers.IO).launch {
                PodologiaDatabase.getDatabase(this@EditAppointmentActivity)
                    .appointmentDao()
                    .update(updated)

                runOnUiThread {
                    Toast.makeText(this@EditAppointmentActivity, "Το ραντεβού ενημερώθηκε", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(this,
            { _, y, m, d ->
                calendar.set(y, m, d)
                editTextDate.setText(dateFmt.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        TimePickerDialog(this,
            { _, h, m ->
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, m)
                editTextTime.setText(timeFmt.format(calendar.time))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }
}
