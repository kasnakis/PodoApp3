package com.kasal.podoapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PatientHistory

class HistoryMedicalFragment : Fragment(), HistorySection {

    private lateinit var etDoctorName: EditText
    private lateinit var etDoctorPhone: EditText
    private lateinit var etDoctorDiagnosis: EditText
    private lateinit var etMedication: EditText
    private lateinit var etAllergies: EditText

    private lateinit var switchDiabetic: Switch
    private lateinit var spinnerDiabeticType: Spinner
    private lateinit var etDiabetesSince: EditText
    private lateinit var etInsulinNotes: EditText
    private lateinit var etPillsNotes: EditText

    private lateinit var switchHasOtherConditions: Switch
    private lateinit var etOtherConditionsNotes: EditText
    private lateinit var etAnticoagulantsNotes: EditText
    private lateinit var etContagiousDiseasesNotes: EditText

    private val diabeticTypes = arrayOf("—", "TYPE_1", "TYPE_2")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_history_medical, container, false)
        bindViews(v)
        setupSpinner()
        setupEnableRules()
        return v
    }

    private fun bindViews(v: View) {
        etDoctorName = v.findViewById(R.id.etDoctorName)
        etDoctorPhone = v.findViewById(R.id.etDoctorPhone)
        etDoctorDiagnosis = v.findViewById(R.id.etDoctorDiagnosis)
        etMedication = v.findViewById(R.id.etMedication)
        etAllergies = v.findViewById(R.id.etAllergies)

        switchDiabetic = v.findViewById(R.id.switchDiabetic)
        spinnerDiabeticType = v.findViewById(R.id.spinnerDiabeticType)
        etDiabetesSince = v.findViewById(R.id.etDiabetesSince)
        etInsulinNotes = v.findViewById(R.id.etInsulinNotes)
        etPillsNotes = v.findViewById(R.id.etPillsNotes)

        switchHasOtherConditions = v.findViewById(R.id.switchHasOtherConditions)
        etOtherConditionsNotes = v.findViewById(R.id.etOtherConditionsNotes)
        etAnticoagulantsNotes = v.findViewById(R.id.etAnticoagulantsNotes)
        etContagiousDiseasesNotes = v.findViewById(R.id.etContagiousNotes)
    }

    private fun setupSpinner() {
        spinnerDiabeticType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            diabeticTypes
        )
    }

    private fun setupEnableRules() {
        switchDiabetic.setOnCheckedChangeListener { _, checked ->
            enableDiabetes(checked)
        }
        switchHasOtherConditions.setOnCheckedChangeListener { _, checked ->
            enableOtherConditions(checked)
        }
        // αρχικό state
        enableDiabetes(false)
        enableOtherConditions(false)
    }

    private fun enableDiabetes(enabled: Boolean) {
        spinnerDiabeticType.isEnabled = enabled
        etDiabetesSince.isEnabled = enabled
        etInsulinNotes.isEnabled = enabled
        etPillsNotes.isEnabled = enabled
        if (!enabled) {
            spinnerDiabeticType.setSelection(0)
            etDiabetesSince.setText("")
            etInsulinNotes.setText("")
            etPillsNotes.setText("")
        }
    }

    private fun enableOtherConditions(enabled: Boolean) {
        etOtherConditionsNotes.isEnabled = enabled
        if (!enabled) etOtherConditionsNotes.setText("")
    }

    // -------- HistorySection --------

    override fun prefill(history: PatientHistory?) {
        if (history == null) return
        etDoctorName.setText(history.doctorName ?: "")
        etDoctorPhone.setText(history.doctorPhone ?: "")
        etDoctorDiagnosis.setText(history.doctorDiagnosis ?: "")
        etMedication.setText(history.medication ?: "")
        etAllergies.setText(history.allergies ?: "")

        switchDiabetic.isChecked = history.isDiabetic
        val idx = diabeticTypes.indexOf(history.diabeticType ?: "—")
        spinnerDiabeticType.setSelection(if (idx >= 0) idx else 0)
        etDiabetesSince.setText(history.diabetesSinceNotes ?: "")
        etInsulinNotes.setText(history.insulinNotes ?: "")
        etPillsNotes.setText(history.pillsNotes ?: "")
        enableDiabetes(history.isDiabetic)

        switchHasOtherConditions.isChecked = history.hasOtherConditions
        etOtherConditionsNotes.setText(history.otherConditionsNotes ?: "")
        etAnticoagulantsNotes.setText(history.anticoagulantsNotes ?: "")
        etContagiousDiseasesNotes.setText(history.contagiousDiseasesNotes ?: "")
        enableOtherConditions(history.hasOtherConditions)
    }

    override fun collectInto(aggr: PatientHistoryAggregator) {
        aggr.doctorName = etDoctorName.text?.toString()
        aggr.doctorPhone = etDoctorPhone.text?.toString()
        aggr.doctorDiagnosis = etDoctorDiagnosis.text?.toString()
        aggr.medication = etMedication.text?.toString()
        aggr.allergies = etAllergies.text?.toString()

        aggr.isDiabetic = switchDiabetic.isChecked
        val sel = diabeticTypes[spinnerDiabeticType.selectedItemPosition]
        aggr.diabeticType = if (sel == "—") null else sel
        aggr.diabetesSinceNotes = etDiabetesSince.text?.toString()
        aggr.insulinNotes = etInsulinNotes.text?.toString()
        aggr.pillsNotes = etPillsNotes.text?.toString()

        aggr.hasOtherConditions = switchHasOtherConditions.isChecked
        aggr.otherConditionsNotes = etOtherConditionsNotes.text?.toString()
        aggr.anticoagulantsNotes = etAnticoagulantsNotes.text?.toString()
        aggr.contagiousDiseasesNotes = etContagiousDiseasesNotes.text?.toString()
    }
}
