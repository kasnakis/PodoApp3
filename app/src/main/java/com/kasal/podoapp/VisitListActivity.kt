package com.kasal.podoapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PodologiaDatabase
import com.kasal.podoapp.data.Visit
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

class VisitListActivity : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var patientId: Int = 0
    private lateinit var list: ListView
    private lateinit var editSearch: EditText
    private lateinit var spinnerSort: Spinner

    private val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    private var currentVisits: List<Visit> = emptyList()
    private var filteredVisits: List<Visit> = emptyList()

    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_list)

        patientId = intent.getIntExtra("patientId", 0)
        if (patientId == 0) {
            Toast.makeText(this, "Δεν βρέθηκε πελάτης", Toast.LENGTH_SHORT).show()
            finish(); return
        }

        list = findViewById(R.id.listViewVisits)
        editSearch = findViewById(R.id.editSearchVisit)
        spinnerSort = findViewById(R.id.spinnerSortVisit)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        list.adapter = adapter

        spinnerSort.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Νεότερα πρώτα", "Παλαιότερα πρώτα", "Χρέωση A-Z", "Χρέωση Z-A")
        )

        val db = PodologiaDatabase.getDatabase(this)
        scope.launch {
            db.visitDao().forPatient(patientId).collectLatest { visits ->
                currentVisits = visits
                applyFilters()
            }
        }

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { applyFilters() }
        })

        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) { applyFilters() }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        list.setOnItemClickListener { _, _, position, _ ->
            val v = filteredVisits.getOrNull(position) ?: return@setOnItemClickListener
            startActivity(Intent(this, VisitDetailActivity::class.java).putExtra("visitId", v.id))
        }

        list.setOnItemLongClickListener { view, anchor, position, _ ->
            val v = filteredVisits.getOrNull(position) ?: return@setOnItemLongClickListener true
            showVisitItemMenu(anchor, v)
            true
        }
    }

    private fun showVisitItemMenu(anchor: View, v: Visit) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.menu_visit_item, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit_visit -> {
                    startActivity(Intent(this, VisitDetailActivity::class.java).putExtra("visitId", v.id))
                    true
                }
                R.id.action_delete_visit -> {
                    confirmDelete(v); true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun confirmDelete(v: Visit) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Διαγραφή επίσκεψης")
            .setMessage("Σίγουρα θέλεις να διαγράψεις αυτή την επίσκεψη;")
            .setPositiveButton("Ναι") { _, _ ->
                scope.launch(Dispatchers.IO) {
                    PodologiaDatabase.getDatabase(this@VisitListActivity).visitDao().delete(v)
                }
            }
            .setNegativeButton("Όχι", null)
            .show()
    }

    private fun applyFilters() {
        val q = editSearch.text?.toString()?.trim()?.lowercase(Locale.getDefault()) ?: ""

        var list = currentVisits
        if (q.isNotEmpty()) {
            list = list.filter { v ->
                val t = v.treatment?.lowercase(Locale.getDefault()) ?: ""
                val n = v.notes?.lowercase(Locale.getDefault()) ?: ""
                val c = v.charge?.lowercase(Locale.getDefault()) ?: ""
                t.contains(q) || n.contains(q) || c.contains(q)
            }
        }

        when (spinnerSort.selectedItemPosition) {
            0 -> list = list.sortedByDescending { it.dateTime } // Νεότερα
            1 -> list = list.sortedBy { it.dateTime }           // Παλαιότερα
            2 -> list = list.sortedBy { it.charge ?: "" }       // Χρέωση A-Z
            3 -> list = list.sortedByDescending { it.charge ?: "" } // Χρέωση Z-A
        }

        filteredVisits = list

        val items = list.map { v ->
            val whenStr = fmt.format(Date(v.dateTime))
            "$whenStr • ${v.treatment ?: "-"} • ${v.charge ?: "-"}"
        }

        adapter.clear()
        adapter.addAll(items)
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
