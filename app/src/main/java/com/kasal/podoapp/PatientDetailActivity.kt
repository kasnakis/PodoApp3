package com.kasal.podoapp.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PatientHistory
import com.kasal.podoapp.data.PodologiaDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class PatientDetailActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var patientId: Int = 0

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

    private lateinit var cbEdemaLeft: CheckBox
    private lateinit var cbEdemaRight: CheckBox
    private lateinit var cbVaricoseDorsalLeft: CheckBox
    private lateinit var cbVaricoseDorsalRight: CheckBox
    private lateinit var cbVaricosePlantarLeft: CheckBox
    private lateinit var cbVaricosePlantarRight: CheckBox

    private lateinit var etSplintNotes: EditText
    private lateinit var spinnerOrthoticType: Spinner

    private lateinit var btnSave: Button

    private val orthoticTypes = arrayOf("NONE", "STOCK", "CUSTOM")
    private val diabeticTypes = arrayOf("", "TYPE_1", "TYPE_2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_detail)

        patientId = intent.getIntExtra("patientId", 0)
        if (patientId == 0) {
            Toast.makeText(this, "Δεν βρέθηκε πελάτης", Toast.LENGTH_SHORT).show()
            finish(); return
        }

        bindViews()
        setupSpinners()

        val db = PodologiaDatabase.getDatabase(this)
        scope.launch {
            val existing = withContext(Dispatchers.IO) {
                db.patientHistoryDao().observeByPatientId(patientId).first()
            }
            if (existing != null) populate(existing)

            btnSave.setOnClickListener {
                val history = collectForm(existing?.id)
                scope.launch(Dispatchers.IO) {
                    db.patientHistoryDao().upsert(history)
                }.invokeOnCompletion {
                    runOnUiThread {
                        Toast.makeText(this@PatientDetailActivity, "Αποθηκεύτηκε το Αναμνηστικό", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
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

        cbEdemaLeft = findViewById(R.id.cbEdemaLeft)
        cbEdemaRight = findViewById(R.id.cbEdemaRight)
        cbVaricoseDorsalLeft = findViewById(R.id.cbVaricoseDorsalLeft)
        cbVaricoseDorsalRight = findViewById(R.id.cbVaricoseDorsalRight)
        cbVaricosePlantarLeft = findViewById(R.id.cbVaricosePlantarLeft)
        cbVaricosePlantarRight = findViewById(R.id.cbVaricosePlantarRight)

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

        cbEdemaLeft.isChecked = h.edemaLeft
        cbEdemaRight.isChecked = h.edemaRight
        cbVaricoseDorsalLeft.isChecked = h.varicoseDorsalLeft
        cbVaricoseDorsalRight.isChecked = h.varicoseDorsalRight
        cbVaricosePlantarLeft.isChecked = h.varicosePlantarLeft
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
            edemaLeft = cbEdemaLeft.isChecked,
            edemaRight = cbEdemaRight.isChecked,
            varicoseDorsalLeft = cbVaricoseDorsalLeft.isChecked,
            varicoseDorsalRight = cbVaricoseDorsalRight.isChecked,
            varicosePlantarLeft = cbVaricosePlantarLeft.isChecked,
            varicosePlantarRight = cbVaricosePlantarRight.isChecked,
            splintNotes = etSplintNotes.text.toString().trim().ifEmpty { null },
            orthoticType = orthoticTypes[spinnerOrthoticType.selectedItemPosition]
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
