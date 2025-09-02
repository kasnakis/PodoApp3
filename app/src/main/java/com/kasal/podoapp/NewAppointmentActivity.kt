package com.kasal.podoapp.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.database.Cursor
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Appointment
import com.kasal.podoapp.data.PodologiaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.cancel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NewAppointmentActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // UI
    private lateinit var textSelectedPatient: TextView
    private lateinit var buttonPickPatient: Button
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var editTextTreatment: EditText
    private lateinit var editTextCharge: EditText
    private lateinit var editTextNotes: EditText
    private lateinit var buttonSave: Button

    // Επιλογή πελάτη
    private var selectedPatientId: Int = 0
    private var selectedPatientName: String? = null
    private val patientIds = ArrayList<Int>()
    private val patientNames = ArrayList<String>()

    // Ημερομηνία/ώρα
    private val cal = Calendar.getInstance() // τοπική ζώνη
    private val fmtDate = SimpleDateFormat("dd/MM/yyyy", Locale("el"))
    private val fmtTime = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_appointment)

        // Bind
        textSelectedPatient = findViewById(R.id.textSelectedPatient)
        buttonPickPatient = findViewById(R.id.buttonPickPatient)
        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)
        editTextTreatment = findViewById(R.id.editTextType)
        editTextCharge = findViewById(R.id.editTextCharge)
        editTextNotes = findViewById(R.id.editTextNotes)
        buttonSave = findViewById(R.id.buttonSave)

        // Defaults: τώρα
        editTextDate.setText(fmtDate.format(cal.time))
        editTextTime.setText(fmtTime.format(cal.time))

        // Αν έρθει preset patientId από αλλού (π.χ. από προφίλ)
        val presetPatientId = intent.getIntExtra("patientId", 0)
        if (presetPatientId != 0) selectedPatientId = presetPatientId

        // Φόρτωσε πελάτες για το dialog
        scope.launch(Dispatchers.IO) {
            loadPatientsForPicker()
            withContext(Dispatchers.Main) {
                // Αν έχουμε preset, βρες όνομα & δείξ' το
                if (selectedPatientId != 0) {
                    val idx = patientIds.indexOf(selectedPatientId)
                    if (idx >= 0) {
                        selectedPatientName = patientNames[idx]
                        textSelectedPatient.text = selectedPatientName
                    }
                }
            }
        }

        // Pickers
        editTextDate.setOnClickListener { showDatePicker() }
        editTextTime.setOnClickListener { showTimePicker() }

        // Επιλογή πελάτη (dialog)
        buttonPickPatient.setOnClickListener {
            if (patientNames.isEmpty()) {
                Toast.makeText(this, "Δεν βρέθηκαν πελάτες. Πρόσθεσε έναν πρώτα.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AlertDialog.Builder(this)
                .setTitle("Επιλογή πελάτη")
                .setItems(patientNames.toTypedArray()) { _, which ->
                    if (which in patientIds.indices) {
                        selectedPatientId = patientIds[which]
                        selectedPatientName = patientNames[which]
                        textSelectedPatient.text = selectedPatientName
                    }
                }
                .show()
        }

        buttonSave.setOnClickListener { save() }
    }

    private fun showDatePicker() {
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        val d = cal.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, { _, yy, mm, dd ->
            cal.set(Calendar.YEAR, yy)
            cal.set(Calendar.MONTH, mm)
            cal.set(Calendar.DAY_OF_MONTH, dd)
            editTextDate.setText(fmtDate.format(cal.time))
        }, y, m, d).show()
    }

    private fun showTimePicker() {
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val min = cal.get(Calendar.MINUTE)
        TimePickerDialog(this, { _, hh, mm ->
            cal.set(Calendar.HOUR_OF_DAY, hh)
            cal.set(Calendar.MINUTE, mm)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            editTextTime.setText(fmtTime.format(cal.time))
        }, h, min, true).show()
    }

    private fun save() {
        // Έλεγχος πελάτη
        if (selectedPatientId == 0) {
            Toast.makeText(this, "Επίλεξε πελάτη", Toast.LENGTH_SHORT).show()
            return
        }

        // Ημερομηνία/ώρα
        val dateStr = editTextDate.text?.toString()?.trim() ?: ""
        val timeStr = editTextTime.text?.toString()?.trim() ?: ""
        if (dateStr.isBlank() || timeStr.isBlank()) {
            Toast.makeText(this, "Συμπλήρωσε ημερομηνία και ώρα", Toast.LENGTH_SHORT).show()
            return
        }

        val dateCal = Calendar.getInstance()
        try {
            val d = fmtDate.parse(dateStr)!!
            dateCal.time = d
        } catch (e: Exception) {
            Toast.makeText(this, "Μη έγκυρη ημερομηνία", Toast.LENGTH_SHORT).show()
            return
        }

        val parts = timeStr.split(":")
        if (parts.size != 2) {
            Toast.makeText(this, "Μη έγκυρη ώρα", Toast.LENGTH_SHORT).show()
            return
        }
        val hh = parts[0].toIntOrNull()
        val mm = parts[1].toIntOrNull()
        if (hh == null || mm == null) {
            Toast.makeText(this, "Μη έγκυρη ώρα", Toast.LENGTH_SHORT).show()
            return
        }
        dateCal.set(Calendar.HOUR_OF_DAY, hh)
        dateCal.set(Calendar.MINUTE, mm)
        dateCal.set(Calendar.SECOND, 0)
        dateCal.set(Calendar.MILLISECOND, 0)
        val dateTimeMillis = dateCal.timeInMillis

        // Λοιπά πεδία
        val treatment = editTextTreatment.text?.toString()?.trim().orEmpty()
        val charge = editTextCharge.text?.toString()?.trim().orEmpty()
        val notes = editTextNotes.text?.toString()?.trim().orEmpty()

        val appointment = Appointment(
            id = 0,
            patientId = selectedPatientId,
            dateTime = dateTimeMillis,
            status = "PENDING",
            treatment = treatment.ifBlank { null },
            charge = charge.ifBlank { null },
            notes = notes.ifBlank { null }
        )

        scope.launch(Dispatchers.IO) {
            val db = PodologiaDatabase.getDatabase(this@NewAppointmentActivity)
            db.appointmentDao().insert(appointment)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@NewAppointmentActivity, "Το ραντεβού καταχωρήθηκε", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Φορτώνει λίστα πελατών με SupportSQLiteDatabase.query(...)
     * 1) id, fullName
     * 2) (fallback) id, firstName, lastName
     * 3) (fallback) id, name
     */
    private fun loadPatientsForPicker() {
        patientIds.clear()
        patientNames.clear()

        val sdb = PodologiaDatabase.getDatabase(this).openHelper.readableDatabase

        fun tryQuery(sql: String, binder: (Cursor) -> Unit): Boolean {
            return try {
                val c = sdb.query(sql)
                c.use { cursor ->
                    while (cursor.moveToNext()) {
                        binder(cursor)
                    }
                }
                true
            } catch (_: Exception) {
                false
            }
        }

        // 1) fullName (το schema σου)
        val okFull = tryQuery(
            "SELECT id, fullName FROM patients ORDER BY fullName"
        ) { c ->
            val id = c.getInt(0)
            val name = c.getString(1) ?: "Χωρίς όνομα"
            patientIds.add(id)
            patientNames.add(name)
        }

        // 2) firstName/lastName
        if (!okFull) {
            val okFirstLast = tryQuery(
                "SELECT id, firstName, lastName FROM patients ORDER BY lastName, firstName"
            ) { c ->
                val id = c.getInt(0)
                val first = c.getString(1) ?: ""
                val last = c.getString(2) ?: ""
                patientIds.add(id)
                patientNames.add(listOf(first, last).filter { it.isNotBlank() }.joinToString(" ").ifBlank { "Χωρίς όνομα" })
            }

            // 3) ενιαίο name
            if (!okFirstLast) {
                tryQuery(
                    "SELECT id, name FROM patients ORDER BY name"
                ) { c ->
                    val id = c.getInt(0)
                    val name = c.getString(1) ?: "Χωρίς όνομα"
                    patientIds.add(id)
                    patientNames.add(name)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
