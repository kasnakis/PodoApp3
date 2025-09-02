package com.kasal.podoapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PodologiaDatabase
import com.kasal.podoapp.data.Visit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VisitListActivity : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var patientId: Int = 0
    private lateinit var list: ListView
    private val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    // Κρατάμε τη λίστα για clicks
    private var currentVisits: List<Visit> = emptyList()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_list)

        patientId = intent.getIntExtra("patientId", 0)
        if (patientId == 0) {
            Toast.makeText(this, "Δεν βρέθηκε πελάτης", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        list = findViewById(R.id.listViewVisits)
        val db = PodologiaDatabase.getDatabase(this)

        // Συλλογή visits σε πραγματικό χρόνο
        scope.launch {
            db.visitDao().forPatient(patientId).collectLatest { visits ->
                currentVisits = visits
                val items = visits.map { v ->
                    val whenStr = fmt.format(Date(v.dateTime))
                    "$whenStr • ${v.treatment ?: "Θεραπεία -"} • ${v.charge ?: "Χρέωση -"}"
                }
                adapter = ArrayAdapter(
                    this@VisitListActivity,
                    android.R.layout.simple_list_item_1,
                    items
                )
                list.adapter = adapter
            }
        }

        // Tap → VisitDetail
        list.setOnItemClickListener { _, _, position, _ ->
            val v = currentVisits.getOrNull(position) ?: return@setOnItemClickListener
            startActivity(
                Intent(this, VisitDetailActivity::class.java)
                    .putExtra("visitId", v.id)
            )
        }

        // Long-press → PopupMenu (Επεξεργασία / Διαγραφή)
        list.setOnItemLongClickListener { parent, view, position, _ ->
            showVisitItemMenu(view, position)
            true
        }
    }

    private fun showVisitItemMenu(anchor: View, position: Int) {
        val v = currentVisits.getOrNull(position) ?: return
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.menu_visit_item, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit_visit -> {
                    startActivity(
                        Intent(this, VisitDetailActivity::class.java)
                            .putExtra("visitId", v.id)
                    )
                    true
                }
                R.id.action_delete_visit -> {
                    confirmDelete(v)
                    true
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
                    PodologiaDatabase.getDatabase(this@VisitListActivity)
                        .visitDao().delete(v)
                    // Δεν χρειάζεται χειροκίνητο refresh: το Flow θα εκπέμψει νέα λίστα
                }
            }
            .setNegativeButton("Όχι", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
