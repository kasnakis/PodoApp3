package com.kasal.podoapp.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
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

class VisitCalendarActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var adapter: VisitForDayAdapter
    private lateinit var textSelectedDate: TextView
    private lateinit var recyclerView: RecyclerView
    private var selectedDate: String = getTodayDate()

    private var currentVisits: List<Visit> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_calendar)

        textSelectedDate = findViewById(R.id.textSelectedDate)
        val buttonPickDate = findViewById<Button>(R.id.buttonPickDate)
        recyclerView = findViewById(R.id.recyclerViewDayVisits)

        adapter = VisitForDayAdapter { visit ->
            openVisitDetail(visit) // TAP → VisitDetail
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        attachLongPressMenu(recyclerView)

        textSelectedDate.text = formatDateForDisplay(selectedDate)
        loadVisitsForDate(selectedDate)

        buttonPickDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Επιλογή Ημερομηνίας")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            picker.show(supportFragmentManager, "visit_calendar_date_picker")

            picker.addOnPositiveButtonClickListener { selection ->
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selection))
                selectedDate = date
                textSelectedDate.text = formatDateForDisplay(date)
                loadVisitsForDate(date)
            }
        }
    }

    private fun loadVisitsForDate(date: String) {
        val db = PodologiaDatabase.getDatabase(this)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val day = try { inputFormat.parse(date) } catch (_: Exception) { null }
        if (day == null) {
            Toast.makeText(this, "Λάθος ημερομηνία", Toast.LENGTH_SHORT).show()
            return
        }
        val cal = Calendar.getInstance().apply { time = day }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        val end = start + 24L * 60L * 60L * 1000L - 1L

        scope.launch(Dispatchers.IO) {
            val visits = db.visitDao().getVisitsForDate(start, end)
            withContext(Dispatchers.Main) {
                currentVisits = visits
                adapter.submit(visits)
            }
        }
    }

    private fun openVisitDetail(visit: Visit) {
        startActivity(
            Intent(this, VisitDetailActivity::class.java)
                .putExtra("visitId", visit.id)
        )
    }

    private fun openPatientProfile(visit: Visit) {
        startActivity(
            Intent(this, PatientDetailActivity::class.java)
                .putExtra("patientId", visit.patientId)
        )
    }

    private fun attachLongPressMenu(rv: RecyclerView) {
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                val child = rv.findChildViewUnder(e.x, e.y) ?: return
                val pos = rv.getChildAdapterPosition(child)
                if (pos == RecyclerView.NO_POSITION) return
                val visit = currentVisits.getOrNull(pos) ?: return
                showItemMenu(child, visit)
            }
        })

        rv.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                gestureDetector.onTouchEvent(e)
                return false
            }
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) { }
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) { }
        })
    }

    private fun showItemMenu(anchor: View, visit: Visit) {
        val popup = androidx.appcompat.widget.PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.menu_visit_calendar_item, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_open_visit -> { openVisitDetail(visit); true }
                R.id.action_open_patient -> { openPatientProfile(visit); true }
                R.id.action_edit_visit -> { openVisitDetail(visit); true } // edit = open detail
                R.id.action_delete_visit -> { confirmDelete(visit); true }
                else -> false
            }
        }
        popup.show()
    }

    private fun confirmDelete(v: Visit) {
        AlertDialog.Builder(this)
            .setTitle("Διαγραφή επίσκεψης")
            .setMessage("Σίγουρα θέλεις να διαγράψεις αυτή την επίσκεψη;")
            .setPositiveButton("Ναι") { _, _ ->
                scope.launch(Dispatchers.IO) {
                    PodologiaDatabase.getDatabase(this@VisitCalendarActivity).visitDao().delete(v)
                    withContext(Dispatchers.Main) { loadVisitsForDate(selectedDate) }
                }
            }
            .setNegativeButton("Όχι", null)
            .show()
    }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun formatDateForDisplay(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE dd/MM/yyyy", Locale("el"))
        return try {
            val parsed = inputFormat.parse(date)
            outputFormat.format(parsed!!)
        } catch (e: Exception) {
            date
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
