package com.kasal.podoapp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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

    private lateinit var textWhen: TextView
    private lateinit var editTreatment: EditText
    private lateinit var editNotes: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button

    private var visit: Visit? = null
    private val dtFmt = SimpleDateFormat("EEEE dd/MM/yyyy, HH:mm", Locale("el"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_detail)

        textWhen = findViewById(R.id.textVisitWhen)
        editTreatment = findViewById(R.id.editTreatment)
        editNotes = findViewById(R.id.editNotes)
        buttonSave = findViewById(R.id.buttonSaveVisit)
        buttonDelete = findViewById(R.id.buttonDeleteVisit)

        val visitId = intent.getIntExtra("visitId", -1)
        if (visitId <= 0) {
            Toast.makeText(this, "Άκυρο visitId", Toast.LENGTH_LONG).show()
            finish(); return
        }

        val db = PodologiaDatabase.getDatabase(this)
        scope.launch(Dispatchers.IO) {
            visit = db.visitDao().getById(visitId)
            withContext(Dispatchers.Main) {
                if (visit == null) {
                    Toast.makeText(this@VisitDetailActivity, "Δεν βρέθηκε επίσκεψη", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    bindUi(visit!!)
                }
            }
        }

        buttonSave.setOnClickListener { saveChanges() }
        buttonDelete.setOnClickListener { confirmDelete() }
    }

    private fun bindUi(v: Visit) {
        textWhen.text = dtFmt.format(Date(v.dateTime))
        editTreatment.setText(v.treatment ?: "")
        editNotes.setText(v.notes ?: "")
    }

    private fun saveChanges() {
        val v = visit ?: return
        val updated = v.copy(
            treatment = editTreatment.text?.toString(),
            notes = editNotes.text?.toString()
        )
        val db = PodologiaDatabase.getDatabase(this)
        scope.launch(Dispatchers.IO) {
            db.visitDao().update(updated)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@VisitDetailActivity, "Αποθηκεύτηκε", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Διαγραφή επίσκεψης")
            .setMessage("Σίγουρα θέλεις να διαγράψεις αυτή την επίσκεψη;")
            .setPositiveButton("Ναι") { _, _ -> deleteVisit() }
            .setNegativeButton("Όχι", null)
            .show()
    }

    private fun deleteVisit() {
        val v = visit ?: return
        val db = PodologiaDatabase.getDatabase(this)
        scope.launch(Dispatchers.IO) {
            db.visitDao().delete(v)
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
