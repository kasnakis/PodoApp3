package com.kasal.podoapp.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PodologiaDatabase
import com.kasal.podoapp.data.Visit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class VisitDetailActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var visitId: Int = 0
    private var loadedVisit: Visit? = null

    private lateinit var tvPatient: TextView
    private lateinit var tvDateTime: TextView
    private lateinit var btnPickDateTime: Button
    private lateinit var etTreatment: EditText
    private lateinit var etCharge: EditText
    private lateinit var etNotes: EditText
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button

    private val cal = Calendar.getInstance()
    private val fmtDate = SimpleDateFormat("dd/MM/yyyy", Locale("el"))
    private val fmtTime = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val fmtFull = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_detail)

        visitId = intent.getIntExtra("visitId", 0)
        if (visitId == 0) {
            Toast.makeText(this, "Δεν βρέθηκε επίσκεψη", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvPatient = findViewById(R.id.tvPatient)
        tvDateTime = findViewById(R.id.tvDateTime)
        btnPickDateTime = findViewById(R.id.btnPickDateTime)
        etTreatment = findViewById(R.id.etTreatment)
        etCharge = findViewById(R.id.etCharge)
        etNotes = findViewById(R.id.etNotes)
        btnSave = findViewById(R.id.btnSave)
        btnDelete = findViewById(R.id.btnDelete)

        btnPickDateTime.setOnClickListener { showPickers() }
        btnSave.setOnClickListener { save() }
        btnDelete.setOnClickListener { deleteVisit() }

        scope.launch(Dispatchers.IO) {
            val db = PodologiaDatabase.getDatabase(this@VisitDetailActivity)
            val visit = db.visitDao().getById(visitId)
            if (visit == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@VisitDetailActivity, "Η επίσκεψη δεν υπάρχει", Toast.LENGTH_SHORT).show()
                    finish()
                }
                return@launch
            }
            loadedVisit = visit

            val patient = db.patientDao().getPatientById(visit.patientId)

            withContext(Dispatchers.Main) {
                tvPatient.text = patient?.fullName ?: "Πελάτης #${visit.patientId}"
                cal.timeInMillis = visit.dateTime
                tvDateTime.text = fmtFull.format(Date(visit.dateTime))
                etTreatment.setText(visit.treatment ?: "")
                etCharge.setText(visit.charge ?: "")
                etNotes.setText(visit.notes ?: "")
            }
        }
    }

    private fun showPickers() {
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        val d = cal.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, yy, mm, dd ->
            cal.set(Calendar.YEAR, yy)
            cal.set(Calendar.MONTH, mm)
            cal.set(Calendar.DAY_OF_MONTH, dd)
            val h = cal.get(Calendar.HOUR_OF_DAY)
            val min = cal.get(Calendar.MINUTE)
            TimePickerDialog(this, { _, hh, mm2 ->
                cal.set(Calendar.HOUR_OF_DAY, hh)
                cal.set(Calendar.MINUTE, mm2)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                tvDateTime.text = fmtFull.format(cal.time)
            }, h, min, true).show()
        }, y, m, d).show()
    }

    private fun save() {
        val v = loadedVisit ?: return
        val updated = v.copy(
            dateTime = cal.timeInMillis,
            treatment = etTreatment.text?.toString()?.trim().orEmpty().ifBlank { null },
            charge = etCharge.text?.toString()?.trim().orEmpty().ifBlank { null },
            notes = etNotes.text?.toString()?.trim().orEmpty().ifBlank { null }
        )
        scope.launch(Dispatchers.IO) {
            PodologiaDatabase.getDatabase(this@VisitDetailActivity).visitDao().update(updated)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@VisitDetailActivity, "Αποθηκεύτηκε", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun deleteVisit() {
        val v = loadedVisit ?: return
        scope.launch(Dispatchers.IO) {
            PodologiaDatabase.getDatabase(this@VisitDetailActivity).visitDao().delete(v)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@VisitDetailActivity, "Διαγράφηκε", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
