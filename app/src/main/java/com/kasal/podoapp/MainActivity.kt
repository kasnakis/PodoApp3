package com.kasal.podoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.kasal.podoapp.ui.AddPatientActivity
import com.kasal.podoapp.ui.PatientListActivity
import com.kasal.podoapp.ui.AppointmentCalendarActivity
import com.kasal.podoapp.ui.VisitCalendarActivity
import com.kasal.podoapp.ui.NewAppointmentActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonAddPatient = findViewById<Button>(R.id.buttonAddPatient)
        val buttonViewPatients = findViewById<Button>(R.id.buttonGo)
        val buttonAppointmentCalendar = findViewById<Button>(R.id.buttonAppointmentCalendar)
        val buttonVisitCalendar = findViewById<Button>(R.id.buttonVisitCalendar)
        val buttonNewAppointment = findViewById<Button>(R.id.buttonNewAppointment)

        buttonAddPatient.setOnClickListener {
            startActivity(Intent(this, AddPatientActivity::class.java))
        }

        buttonViewPatients.setOnClickListener {
            startActivity(Intent(this, PatientListActivity::class.java))
        }

        buttonAppointmentCalendar.setOnClickListener {
            startActivity(Intent(this, AppointmentCalendarActivity::class.java))
        }

        buttonVisitCalendar.setOnClickListener {
            startActivity(Intent(this, VisitCalendarActivity::class.java))
        }

        buttonNewAppointment.setOnClickListener {
            startActivity(Intent(this, NewAppointmentActivity::class.java))
        }
    }
}
