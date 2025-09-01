package com.kasal.podoapp.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Patient
import com.kasal.podoapp.data.PodologiaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPatientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)

        val nameEditText = findViewById<EditText>(R.id.editTextFullName)
        val phoneEditText = findViewById<EditText>(R.id.editTextPhone)
        val cardCodeEditText = findViewById<EditText>(R.id.editTextCardCode)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val birthDateEditText = findViewById<EditText>(R.id.editTextBirthDate)
        val professionEditText = findViewById<EditText>(R.id.editTextProfession)
        val notesEditText = findViewById<EditText>(R.id.editTextNotes)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        val db = PodologiaDatabase.getDatabase(this)

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val cardCode = cardCodeEditText.text.toString().trim().ifEmpty { null }
            val email = emailEditText.text.toString().trim().ifEmpty { null }
            val birthDate = birthDateEditText.text.toString().trim().ifEmpty { null }
            val profession = professionEditText.text.toString().trim().ifEmpty { null }
            val notes = notesEditText.text.toString().trim().ifEmpty { null }

            if (name.isBlank()) {
                Toast.makeText(this, "Το όνομα είναι υποχρεωτικό", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val patient = Patient(
                fullName = name,
                phone = phone,
                notes = notes,
                cardCode = cardCode,
                email = email,
                birthDate = birthDate,
                profession = profession
            )

            CoroutineScope(Dispatchers.IO).launch {
                db.patientDao().insert(patient)
                runOnUiThread {
                    Toast.makeText(this@AddPatientActivity, "Ο πελάτης αποθηκεύτηκε", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
