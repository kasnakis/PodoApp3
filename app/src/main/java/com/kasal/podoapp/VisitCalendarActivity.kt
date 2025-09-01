package com.kasal.podoapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PodologiaDatabase
import com.kasal.podoapp.data.Visit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class VisitCalendarActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: VisitForDayAdapter

    private var currentDayStart: Long = 0L
    private var currentDayEnd: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_calendar)

        // Προσοχή: χρησιμοποίησε το ID που έχεις στο XML σου
        recycler = findViewById(R.id.recyclerViewDayVisits)
        recycler.layoutManager = LinearLayoutManager(this)

        // Ο δικός σου VisitForDayAdapter απαιτεί lambda (Visit) -> Unit
        adapter = VisitForDayAdapter { visit: Visit ->
            // TODO: Άνοιξε οθόνη λεπτομερειών επίσκεψης ή δείξε Toast
            // startActivity(Intent(this, VisitDetailActivity::class.java).putExtra("visitId", visit.id))
        }
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
        val visitDao = db.visitDao()

        lifecycleScope.launch {
            val visits = withContext(Dispatchers.IO) {
                visitDao.getVisitsForDate(currentDayStart, currentDayEnd)
            }
            // Ο adapter σου έχει submitList(...)
            adapter.submitList(visits)
        }
    }
}
