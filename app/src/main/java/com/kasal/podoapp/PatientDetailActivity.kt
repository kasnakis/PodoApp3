package com.kasal.podoapp.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Patient
import com.kasal.podoapp.data.PatientHistory
import com.kasal.podoapp.data.PodologiaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PatientDetailActivity : AppCompatActivity() {

    private var patientId: Int = 0
    private var existing: PatientHistory? = null

    // Views
    private lateinit var etDoctorName: EditText
    private lateinit var etDoctorPhone: EditText
    private lateinit var etDiagnosis: EditText
    private lateinit var etMedication: EditText
    private lateinit var etAllergies: EditText

    private lateinit var switchDiabetic: Switch
    private lateinit var spinnerDiabeticType: Spinner
    private lateinit var etInsulinNotes: EditText
    private lateinit var etPillsNotes: EditText

    private lateinit var cbMetatarsalDrop: CheckBox
    private lateinit var cbValgus: CheckBox
    private lateinit var cbVarus: CheckBox
    private lateinit var cbEquinus: CheckBox
    private lateinit var cbCavus: CheckBox
    private lateinit var cbFlatfoot: CheckBox
    private lateinit var cbPronation: CheckBox
    private lateinit var cbSupination: CheckBox

    // Vascular (Right) – τα υπόλοιπα tabs τα χειριζόμαστε στα αντίστοιχα fragments
    private lateinit var cbEdemaRight: CheckBox
    private lateinit var cbVaricoseDorsalRight: CheckBox
    private lateinit var cbVaricosePlantarRight: CheckBox

    private lateinit var etSplintNotes: EditText
    private lateinit var spinnerOrthoticType: Spinner

    private lateinit var btnSave: Button

    private val orthoticTypes = arrayOf("NONE", "STOCK", "CUSTOM")
    private val diabeticTypes = arrayOf("", "TYPE_1", "TYPE_2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_detail)

        // ΝΕΟ (safe):
        patientId = intent.getIntExtra("patientId", -1)
        if (patientId <= 0) {
            Toast.makeText(this, "Άκυρο patientId", Toast.LENGTH_LONG).show()
            finish(); return
        }

        bindViews()
        setupSpinners()

        val db = PodologiaDatabase.getDatabase(this)

        // Φόρτωση υπάρχοντος ιστορικού (αν υπάρχει)
        lifecycleScope.launch {
            existing = withContext(Dispatchers.IO) {
                db.patientHistoryDao().getByPatientId(patientId)
            }
            existing?.let { populate(it) }

            btnSave.setOnClickListener {
                val history = collectForm(existing?.id)
                lifecycleScope.launch(Dispatchers.IO) {
                    db.patientHistoryDao().upsert(history)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@PatientDetailActivity,
                            "Αποθηκεύτηκε το Αναμνηστικό",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        }
    }

    // Helper: εντοπίζει CheckBox δοκιμάζοντας πιθανά ids (τρέχον + παλιά/typos)
    private fun findCheckBox(vararg names: String): CheckBox {
        for (name in names) {
            val id = resources.getIdentifier(name, "id", packageName)
            if (id != 0) {
                val v: CheckBox? = findViewById(id)
                if (v != null) return v
            }
        }
        error("Δεν βρέθηκε κανένα από τα ids: ${names.joinToString()}")
    }

    private fun bindViews() {
        etDoctorName = findViewById(R.id.etDoctorName)
        etDoctorPhone = findViewById(R.id.etDoctorPhone)
        etDiagnosis = findViewById(R.id.etDiagnosis)
        etMedication = findViewById(R.id.etMedication)
        etAllergies = findViewById(R.id.etAllergies)

        switchDiabetic = findViewById(R.id.switchDiabetic)
        spinnerDiabeticType = findViewById(R.id.spinnerDiabeticType)
        etInsulinNotes = findViewById(R.id.etInsulinNotes)
        etPillsNotes = findViewById(R.id.etPillsNotes)

        cbMetatarsalDrop = findViewById(R.id.cbMetatarsalDrop)
        cbValgus = findViewById(R.id.cbValgus)
        cbVarus = findViewById(R.id.cbVarus)
        cbEquinus = findViewById(R.id.cbEquinus)
        cbCavus = findViewById(R.id.cbCavus)
        cbFlatfoot = findViewById(R.id.cbFlatfoot)
        cbPronation = findViewById(R.id.cbPronation)
        cbSupination = findViewById(R.id.cbSupination)

        // Δοκιμάζουμε πρώτα τα σωστά ids, μετά παλιότερα/typos για συμβατότητα
        cbEdemaRight = findCheckBox("cbEdemaRight", "cbedemaRight", "cbEdema_right")
        cbVaricoseDorsalRight = findCheckBox("cbVaricoseDorsalRight", "cbvaricoseDorsalRight")
        cbVaricosePlantarRight = findCheckBox("cbVaricosePlantarRight", "cbvaricosePlantarRight")

        etSplintNotes = findViewById(R.id.etSplintNotes)
        spinnerOrthoticType = findViewById(R.id.spinnerOrthoticType)

        btnSave = findViewById(R.id.btnSaveHistory)
    }

    private fun setupSpinners() {
        spinnerDiabeticType.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, diabeticTypes)
        spinnerOrthoticType.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, orthoticTypes)
    }

    private fun populate(h: PatientHistory) {
        etDoctorName.setText(h.doctorName ?: "")
        etDoctorPhone.setText(h.doctorPhone ?: "")
        etDiagnosis.setText(h.doctorDiagnosis ?: "")
        etMedication.setText(h.medication ?: "")
        etAllergies.setText(h.allergies ?: "")

        switchDiabetic.isChecked = h.isDiabetic
        spinnerDiabeticType.setSelection(diabeticTypes.indexOf(h.diabeticType ?: ""))
        etInsulinNotes.setText(h.insulinNotes ?: "")
        etPillsNotes.setText(h.pillsNotes ?: "")

        cbMetatarsalDrop.isChecked = h.metatarsalDrop
        cbValgus.isChecked = h.valgus
        cbVarus.isChecked = h.varus
        cbEquinus.isChecked = h.equinus
        cbCavus.isChecked = h.cavus
        cbFlatfoot.isChecked = h.flatfoot
        cbPronation.isChecked = h.pronation
        cbSupination.isChecked = h.supination

        // Vascular (Right)
        cbEdemaRight.isChecked = h.edemaRight
        cbVaricoseDorsalRight.isChecked = h.varicoseDorsalRight
        cbVaricosePlantarRight.isChecked = h.varicosePlantarRight

        etSplintNotes.setText(h.splintNotes ?: "")
        spinnerOrthoticType.setSelection(orthoticTypes.indexOf(h.orthoticType ?: "NONE"))
    }

    private fun collectForm(existingId: Int?): PatientHistory {
        return PatientHistory(
            id = existingId ?: 0,
            patientId = patientId,

            doctorName = etDoctorName.text.toString().trim().ifEmpty { null },
            doctorPhone = etDoctorPhone.text.toString().trim().ifEmpty { null },
            doctorDiagnosis = etDiagnosis.text.toString().trim().ifEmpty { null },
            medication = etMedication.text.toString().trim().ifEmpty { null },
            allergies = etAllergies.text.toString().trim().ifEmpty { null },

            isDiabetic = switchDiabetic.isChecked,
            diabeticType = diabeticTypes[spinnerDiabeticType.selectedItemPosition].ifEmpty { null },
            insulinNotes = etInsulinNotes.text.toString().trim().ifEmpty { null },
            pillsNotes = etPillsNotes.text.toString().trim().ifEmpty { null },

            metatarsalDrop = cbMetatarsalDrop.isChecked,
            valgus = cbValgus.isChecked,
            varus = cbVarus.isChecked,
            equinus = cbEquinus.isChecked,
            cavus = cbCavus.isChecked,
            flatfoot = cbFlatfoot.isChecked,
            pronation = cbPronation.isChecked,
            supination = cbSupination.isChecked,

            // προς το παρόν μόνο τα right-vascular από αυτό το Activity
            edemaLeft = existing?.edemaLeft ?: false, // δεν υπάρχει control εδώ
            edemaRight = cbEdemaRight.isChecked,
            varicoseDorsalLeft = existing?.varicoseDorsalLeft ?: false,
            varicoseDorsalRight = cbVaricoseDorsalRight.isChecked,
            varicosePlantarLeft = existing?.varicosePlantarLeft ?: false,
            varicosePlantarRight = cbVaricosePlantarRight.isChecked,

            splintNotes = etSplintNotes.text.toString().trim().ifEmpty { null },
            orthoticType = orthoticTypes[spinnerOrthoticType.selectedItemPosition]
        )
    }
}