package com.kasal.podoapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PatientHistory

class HistoryMedicalFragment : Fragment(), HistorySection {

    private lateinit var etDoctorName: EditText
    private lateinit var etDoctorPhone: EditText
    private lateinit var etDiagnosis: EditText
    private lateinit var etMedication: EditText
    private lateinit var etAllergies: EditText
    private lateinit var swDiabetic: Switch
    private lateinit var spDiabeticType: Spinner
    private lateinit var etInsulinNotes: EditText
    private lateinit var etPillsNotes: EditText
    private lateinit var etOtherConditions: EditText
    private lateinit var etAnticoagulants: EditText
    private lateinit var etContagious: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history_medical, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        etDoctorName = v.findViewById(R.id.etDoctorName)
        etDoctorPhone = v.findViewById(R.id.etDoctorPhone)
        etDiagnosis = v.findViewById(R.id.etDiagnosis)
        etMedication = v.findViewById(R.id.etMedication)
        etAllergies = v.findViewById(R.id.etAllergies)
        swDiabetic = v.findViewById(R.id.switchDiabetic)
        spDiabeticType = v.findViewById(R.id.spinnerDiabeticType)
        etInsulinNotes = v.findViewById(R.id.etInsulinNotes)
        etPillsNotes = v.findViewById(R.id.etPillsNotes)
        etOtherConditions = v.findViewById(R.id.etOtherConditions)
        etAnticoagulants = v.findViewById(R.id.etAnticoagulants)
        etContagious = v.findViewById(R.id.etContagious)

        spDiabeticType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            arrayOf("", "TYPE_1", "TYPE_2")
        )
    }

    override fun prefill(history: PatientHistory?) {
        history ?: return
        etDoctorName.setText(history.doctorName ?: "")
        etDoctorPhone.setText(history.doctorPhone ?: "")
        etDiagnosis.setText(history.doctorDiagnosis ?: "")
        etMedication.setText(history.medication ?: "")
        etAllergies.setText(history.allergies ?: "")
        swDiabetic.isChecked = history.isDiabetic
        val idx = arrayOf("", "TYPE_1", "TYPE_2").indexOf(history.diabeticType ?: "")
        spDiabeticType.setSelection(if (idx >= 0) idx else 0)
        etInsulinNotes.setText(history.insulinNotes ?: "")
        etPillsNotes.setText(history.pillsNotes ?: "")
        etOtherConditions.setText(history.otherConditions ?: "")
        etAnticoagulants.setText(history.anticoagulantsNotes ?: "")
        etContagious.setText(history.contagiousDiseasesNotes ?: "")
    }

    override fun collectInto(aggr: PatientHistoryAggregator) {
        aggr.doctorName = etDoctorName.text.toString().trim().ifEmpty { null }
        aggr.doctorPhone = etDoctorPhone.text.toString().trim().ifEmpty { null }
        aggr.doctorDiagnosis = etDiagnosis.text.toString().trim().ifEmpty { null }
        aggr.medication = etMedication.text.toString().trim().ifEmpty { null }
        aggr.allergies = etAllergies.text.toString().trim().ifEmpty { null }
        aggr.isDiabetic = swDiabetic.isChecked
        aggr.diabeticType = arrayOf("", "TYPE_1", "TYPE_2")[spDiabeticType.selectedItemPosition].ifEmpty { null }
        aggr.insulinNotes = etInsulinNotes.text.toString().trim().ifEmpty { null }
        aggr.pillsNotes = etPillsNotes.text.toString().trim().ifEmpty { null }
        aggr.otherConditions = etOtherConditions.text.toString().trim().ifEmpty { null }
        aggr.anticoagulantsNotes = etAnticoagulants.text.toString().trim().ifEmpty { null }
        aggr.contagiousDiseasesNotes = etContagious.text.toString().trim().ifEmpty { null }
    }
}
