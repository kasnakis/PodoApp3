package com.kasal.podoapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PodologiaDatabase
import com.kasal.podoapp.data.Visit
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class VisitCalendarActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var adapter: VisitForDayAdapter
    private lateinit var textSelectedDate: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonPickDate: Button

    private var selectedDayUtcMillis: Long = todayUtcStartMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_calendar)

        textSelectedDate = findViewById(R.id.textSelectedDate)
        recyclerView = findViewById(R.id.recyclerViewDayVisits)
        buttonPickDate = findViewById(R.id.buttonPickDate)

        adapter = VisitForDayAdapter { visit -> openVisitDetail(visit) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        textSelectedDate.text = formatDateForDisplay(selectedDayUtcMillis)
        loadVisitsForDay(selectedDayUtcMillis)

        buttonPickDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Επιλογή Ημερομηνίας")
                .setSelection(selectedDayUtcMillis)
                .build()

            picker.show(supportFragmentManager, "date_picker")
            picker.addOnPositiveButtonClickListener { selectionUtc ->
                selectedDayUtcMillis = startOfUtcDay(selectionUtc)
                textSelectedDate.text = formatDateForDisplay(selectedDayUtcMillis)
                loadVisitsForDay(selectedDayUtcMillis)
            }
        }
    }

    private fun openVisitDetail(visit: Visit) {
        val i = Intent(this, VisitDetailActivity::class.java)
        i.putExtra("visitId", visit.id)
        startActivity(i)
    }

    private fun loadVisitsForDay(dayUtcStart: Long) {
        val (start, end) = dayBoundsUtc(dayUtcStart)
        val db = PodologiaDatabase.getDatabase(this)

        scope.launch(Dispatchers.IO) {
            val visits = db.visitDao().getVisitsForDate(start, end)
            withContext(Dispatchers.Main) {
                adapter.submitList(visits)   // ✅ σιγουρεύουμε ότι υπάρχει αυτή η μέθοδος στον adapter
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun todayUtcStartMillis(): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun dayBoundsUtc(dayStartUtc: Long): Pair<Long, Long> {
        val start = dayStartUtc
        val end = dayStartUtc + (24L * 60L * 60L * 1000L) - 1L
        return start to end
    }

    private fun startOfUtcDay(anyUtcMillis: Long): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = anyUtcMillis }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun formatDateForDisplay(dayStartUtc: Long): String {
        val local = Date(dayStartUtc)
        val fmt = SimpleDateFormat("EEEE dd/MM/yyyy", Locale("el"))
        return fmt.format(local)
    }
}
