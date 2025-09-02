package com.kasal.podoapp.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.database.Cursor
import android.os.Bundle
import android.widget.*
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

    private lateinit var spinnerPatient: Spinner
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var editTextTreatment: EditText
    private lateinit var editTextCharge: EditText
    private lateinit var editTextNotes: EditText
    private lateinit var buttonSave: Button

    private val cal = Calendar.getInstance() // τοπική ζώνη

    // Από προφίλ πελάτη μπορεί να έρθει preset
    private var presetPatientId: Int = 0

    // Λίστα για spinner
    private val patientIds = ArrayList<Int>()
    private val patientNames = ArrayList<String>()

    private val fmtDate = SimpleDateFormat("dd/MM/yyyy", Locale("el"))
    private val fmtTime = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_appointment)

        spinnerPatient = findViewById(R.id.spinnerPatient)
        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)
        editTextTreatment = findViewById(R.id.editTextType) // κρατάμε το ίδιο id που ήδη έχεις
        editTextCharge = findViewById(R.id.editTextCharge)
        editTextNotes = findViewById(R.id.editTextNotes)
        buttonSave = findViewById(R.id.buttonSave)

        // Defaults: τώρα
        editTextDate.setText(fmtDate.format(cal.time))
        editTextTime.setText(fmtTime.format(cal.time))

        editTextDate.setOnClickListener { showDatePicker() }
        editTextTime.setOnClickListener { showTimePicker() }

        presetPatientId = intent.getIntExtra("patientId", 0)

        // Γέμισμα spinner από DB (ωμά, για να μη δεσμευτούμε στο όνομα DAO μεθόδου)
        scope.launch(Dispatchers.IO) {
            loadPatientsForSpinner()
            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@NewAppointmentActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    patientNames
                )
                spinnerPatient.adapter = adapter

                // Αν έχουμε presetId, προεπιλογή
                if (presetPatientId != 0) {
                    val idx = patientIds.indexOf(presetPatientId)
                    if (idx >= 0) spinnerPatient.setSelection(idx)
                }
            }
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
        // patientId
        val pos = spinnerPatient.selectedItemPosition
        val patientId = if (pos in patientIds.indices) patientIds[pos] else 0
        if (patientId == 0) {
            Toast.makeText(this, "Επίλεξε πελάτη", Toast.LENGTH_SHORT).show()
            return
        }

        // Συνδυάζουμε Date + Time σε ΤΟΠΙΚΟ ημερολόγιο → epoch ms
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

        val treatment = editTextTreatment.text?.toString()?.trim().orEmpty()
        val charge = editTextCharge.text?.toString()?.trim().orEmpty()
        val notes = editTextNotes.text?.toString()?.trim().orEmpty()

        val appointment = Appointment(
            id = 0,
            patientId = patientId,
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
     * Φορτώνει λίστα πελατών με SupportSQLiteDatabase.query(...) ώστε να μη δεσμευόμαστε σε συγκεκριμένο DAO.
     * Προσπαθούμε πρώτα (id, firstName, lastName). Αν αποτύχει, δοκιμάζουμε (id, name).
     */
    private fun loadPatientsForSpinner() {
        patientIds.clear()
        patientNames.clear()

        val sdb = PodologiaDatabase.getDatabase(this).openHelper.readableDatabase

        fun tryQuery(sql: String, binder: (Cursor) -> Unit): Boolean {
            return try {
                val cursor = sdb.query(sql) // <-- ΟΧΙ rawQuery: SupportSQLiteDatabase.query
                cursor.use { c ->
                    while (c.moveToNext()) {
                        binder(c)
                    }
                }
                true
            } catch (_: Exception) {
                false
            }
        }

        // 1η προσπάθεια: firstName/lastName
        val ok1 = tryQuery(
            "SELECT id, firstName, lastName FROM patients ORDER BY lastName, firstName"
        ) { c ->
            val id = c.getInt(0)
            val first = c.getString(1) ?: ""
            val last = c.getString(2) ?: ""
            patientIds.add(id)
            patientNames.add(listOf(first, last).filter { it.isNotBlank() }.joinToString(" "))
        }

        if (!ok1) {
            // 2η προσπάθεια: ενιαίο name
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

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
