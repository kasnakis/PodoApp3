package com.kasal.podoapp.ui

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PodologiaDatabase
import com.kasal.podoapp.data.Visit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NewVisitActivity : AppCompatActivity() {

    private lateinit var editTextDate: EditText
    private lateinit var editTextReason: EditText
    private lateinit var editTextDiagnosis: EditText
    private lateinit var editTextTreatment: EditText
    private lateinit var editTextNotes: EditText
    private lateinit var buttonSave: Button

    private val calendar = Calendar.getInstance()
    private var patientId: Int = 0  // Από την PatientDetailActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_visit)

        editTextDate = findViewById(R.id.editTextVisitDate)
        editTextReason = findViewById(R.id.editTextVisitReason)
        editTextDiagnosis = findViewById(R.id.editTextVisitDiagnosis)
        editTextTreatment = findViewById(R.id.editTextVisitTreatment)
        editTextNotes = findViewById(R.id.editTextVisitNotes)
        buttonSave = findViewById(R.id.buttonSaveVisit)

        patientId = intent.getIntExtra("patientId", 0)

        editTextDate.setOnClickListener { showDatePicker() }

        buttonSave.setOnClickListener {
            saveVisit()
        }
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

    private fun saveVisit() {
        val date = editTextDate.text.toString().trim()
        val reason = editTextReason.text.toString().trim()
        val diagnosis = editTextDiagnosis.text.toString().trim()
        val treatment = editTextTreatment.text.toString().trim()
        val notes = editTextNotes.text.toString().trim()

        if (date.isBlank() || reason.isBlank() || diagnosis.isBlank() || treatment.isBlank()) {
            Toast.makeText(this, "Συμπληρώστε όλα τα υποχρεωτικά πεδία", Toast.LENGTH_SHORT).show()
            return
        }

        val visit = Visit(
            patientId = patientId,
            date = date,
            reason = reason,
            diagnosis = diagnosis,
            treatment = treatment,
            notes = if (notes.isBlank()) null else notes,
            photoUris = emptyList()
            // Μπορούμε να προσθέσουμε Gallery support αργότερα
        )

        CoroutineScope(Dispatchers.IO).launch {
            val db = PodologiaDatabase.getDatabase(this@NewVisitActivity)
            db.visitDao().insert(visit)
            runOnUiThread {
                Toast.makeText(this@NewVisitActivity, "Η επίσκεψη καταχωρήθηκε", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
