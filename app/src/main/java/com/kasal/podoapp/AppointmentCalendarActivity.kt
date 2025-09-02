package com.kasal.podoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.kasal.podoapp.data.Appointment
import com.kasal.podoapp.data.PodoAppDatabase
import com.kasal.podoapp.ui.theme.PodoAppTheme
import com.kasal.podoapp.ui.components.AppointmentCalendarScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class AppointmentCalendarActivity : ComponentActivity() {

    private lateinit var db: PodoAppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = PodoAppDatabase.getInstance(this)
        val appointmentDao = db.appointmentDao()
        val patientDao = db.patientDao()

        val selectedDate = LocalDate.now()

        val appointmentsFlow = appointmentDao.getAppointmentsForDate(selectedDate)

        val appointmentsWithPatientNamesFlow: Flow<List<Pair<Appointment, String>>> =
            appointmentsFlow.flatMapLatest { appointments ->
                val flows = appointments.map { appointment ->
                    patientDao.getPatientById(appointment.patientId).combine(appointmentDao.getAppointmentsForDate(selectedDate)) { patient, _ ->
                        Pair(appointment, patient.firstName + " " + patient.lastName)
                    }
                }
                combine(flows) { it.toList() }
            }

        setContent {
            PodoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val appointmentsWithPatientNames by appointmentsWithPatientNamesFlow.collectAsState(initial = emptyList())

                    val appointments = appointmentsWithPatientNames.map { it.first }

                    AppointmentCalendarScreen(
                        appointments = appointments,
                        onAddAppointment = { date ->
                            // TODO: Add your logic here to add a new appointment
                        },
                        onAppointmentSelected = { appointment ->
                            // TODO: Add your logic here when an appointment is selected
                        },
                        onConvertToVisit = { appointment ->
                            Log.d("AppointmentCalendar", "Convert to visit: ${appointment.appointmentId}")
                            val intent = Intent(this, VisitDetailActivity::class.java).apply {
                                putExtra("patientId", appointment.patientId)
                            }
                            startActivity(intent)
                        },
                        onEdit = { appointment ->
                            Log.d("AppointmentCalendar", "Edit appointment: ${appointment.appointmentId}")
                            val intent = Intent(this, EditAppointmentActivity::class.java).apply {
                                putExtra("appointmentId", appointment.appointmentId)
                            }
                            startActivity(intent)
                        },
                        onDelete = { appointment ->
                            Log.d("AppointmentCalendar", "Delete appointment: ${appointment.appointmentId}")
                            lifecycleScope.launch {
                                appointmentDao.delete(appointment)
                            }
                        }
                    )
                }
            }
        }
    }
}