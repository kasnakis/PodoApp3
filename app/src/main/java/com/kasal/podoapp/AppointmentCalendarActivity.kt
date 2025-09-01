package com.kasal.podoapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Appointment
import com.kasal.podoapp.data.PodologiaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class AppointmentCalendarActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: AppointmentForDayAdapter

    private var currentDayStart: Long = 0L
    private var currentDayEnd: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_calendar)

        // Προσοχή: χρησιμοποίησε το ID που έχεις στο XML σου
        recycler = findViewById(R.id.recyclerViewDayAppointments)
        recycler.layoutManager = LinearLayoutManager(this)

        // Ο δικός σου adapter ΔΕΝ παίρνει callbacks στον constructor
        adapter = AppointmentForDayAdapter()
        recycler.adapter = adapter

        setTodayBounds()
        reloadSelectedDay()
    }

    private fun setTodayBounds() {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        currentDayStart = cal.timeInMillis
        cal.add(Calendar.DAY_OF_MONTH, 1)
        currentDayEnd = cal.timeInMillis - 1
    }

    private fun reloadSelectedDay() {
        val db = PodologiaDatabase.getDatabase(this)
        val apptDao = db.appointmentDao()
        val patientDao = db.patientDao()

        lifecycleScope.launch {
            val list: List<Appointment> = withContext(Dispatchers.IO) {
                // Χρησιμοποιούμε το DAO που δώσαμε πριν
                apptDao.getAppointmentsForDate(currentDayStart, currentDayEnd)
            }

            // Ο δικός σου AppointmentForDayAdapter κρατάει List<Pair<Appointment, String>>
            // όπου το δεύτερο στοιχείο είναι συνήθως το όνομα πελάτη
            val pairs: List<Pair<Appointment, String>> = withContext(Dispatchers.IO) {
                list.map { a ->
                    val p = patientDao.getById(a.patientId)
                    val name = p?.fullName ?: "—"
                    a to name
                }
            }

            // Ο adapter σου έχει submitList(...)
            adapter.submitList(pairs)
        }.invokeOnCompletion {
            if (it != null) {
                Toast.makeText(this, "Σφάλμα: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
