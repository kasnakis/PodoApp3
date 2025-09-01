package com.kasal.podoapp.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.R
import com.kasal.podoapp.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.util.*

class AppointmentActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var patientId: Int = 0

    private lateinit var tvDateTime: TextView
    private lateinit var etNotes: EditText
    private lateinit var etCharge: EditText
    private lateinit var etTreatment: EditText
    private lateinit var btnPick: Button
    private lateinit var btnSave: Button
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: AppointmentAdapter

    private var selectedMillis: Long = System.currentTimeMillis()

    private lateinit var db: PodologiaDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        patientId = intent.getIntExtra("patientId", 0)
        if (patientId == 0) { Toast.makeText(this, "Δεν βρέθηκε πελάτης", Toast.LENGTH_SHORT).show(); finish(); return }

        db = PodologiaDatabase.getDatabase(this)

        tvDateTime = findViewById(R.id.tvDateTime)
        etNotes = findViewById(R.id.etApptNotes)
        etCharge = findViewById(R.id.etApptCharge)
        etTreatment = findViewById(R.id.etApptTreatment)
        btnPick = findViewById(R.id.btnPickDateTime)
        btnSave = findViewById(R.id.btnSaveAppt)
        recycler = findViewById(R.id.recyclerAppointments)

        recycler.layoutManager = LinearLayoutManager(this)
        adapter = AppointmentAdapter(
            onCompleted = { appt -> completeAppointment(appt) }
        )
        recycler.adapter = adapter

        updateDateTimeLabel()

        btnPick.setOnClickListener { pickDateTime() }
        btnSave.setOnClickListener { saveAppointment() }

        scope.launch {
            db.appointmentDao().forPatient(patientId).collectLatest { list ->
                adapter.submit(list)
            }
        }
    }

    private fun pickDateTime() {
        val cal = Calendar.getInstance().apply { timeInMillis = selectedMillis }
        DatePickerDialog(this, { _, y, m, d ->
            cal.set(Calendar.YEAR, y); cal.set(Calendar.MONTH, m); cal.set(Calendar.DAY_OF_MONTH, d)
            TimePickerDialog(this, { _, h, min ->
                cal.set(Calendar.HOUR_OF_DAY, h); cal.set(Calendar.MINUTE, min); cal.set(Calendar.SECOND, 0)
                selectedMillis = cal.timeInMillis
                updateDateTimeLabel()
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateDateTimeLabel() {
        tvDateTime.text = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date(selectedMillis))
    }

    private fun saveAppointment() {
        val notes = etNotes.text.toString().trim().ifEmpty { null }
        val charge = etCharge.text.toString().trim().ifEmpty { null }
        val treatment = etTreatment.text.toString().trim().ifEmpty { null }

        val appt = Appointment(
            patientId = patientId,
            dateTime = selectedMillis,
            notes = notes,
            charge = charge,
            treatment = treatment,
            status = "SCHEDULED"
        )

        scope.launch(Dispatchers.IO) {
            db.appointmentDao().insert(appt)
        }.invokeOnCompletion {
            runOnUiThread {
                Toast.makeText(this, "Το ραντεβού καταχωρήθηκε", Toast.LENGTH_SHORT).show()
                etNotes.setText(""); etCharge.setText(""); etTreatment.setText("")
            }
        }
    }

    private fun completeAppointment(appt: Appointment) {
        scope.launch(Dispatchers.IO) {
            db.appointmentDao().updateStatus(appt.id, "COMPLETED")
            db.visitDao().insert(
                Visit(
                    patientId = appt.patientId,
                    appointmentId = appt.id,
                    dateTime = System.currentTimeMillis(),
                    notes = appt.notes,
                    charge = appt.charge,
                    treatment = appt.treatment
                )
            )
        }.invokeOnCompletion {
            runOnUiThread { Toast.makeText(this, "Ολοκληρώθηκε και δημιουργήθηκε επίσκεψη", Toast.LENGTH_SHORT).show() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
