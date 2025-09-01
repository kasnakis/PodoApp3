package com.kasal.podoapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Switch
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
    private lateinit var etInsulinNotes: EditText
    private lateinit var etPillsNotes: EditText

    private lateinit var etOtherConditions: EditText
    private lateinit var etAnticoagulantsNotes: EditText
    private lateinit var etContagiousDiseasesNotes: EditText

    private val diabeticTypes = arrayOf("", "TYPE_1", "TYPE_2")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history_medical, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        etDoctorName = v.findViewById(R.id.etDoctorName)
        etDoctorPhone = v.findViewById(R.id.etDoctorPhone)
        etDoctorDiagnosis = v.findViewById(R.id.etDoctorDiagnosis)
        etMedication = v.findViewById(R.id.etMedication)
        etAllergies = v.findViewById(R.id.etAllergies)

        switchDiabetic = v.findViewById(R.id.switchDiabetic)
        spinnerDiabeticType = v.findViewById(R.id.spinnerDiabeticType)
        etInsulinNotes = v.findViewById(R.id.etInsulinNotes)
        etPillsNotes = v.findViewById(R.id.etPillsNotes)

        etOtherConditions = v.findViewById(R.id.etOtherConditions)
        etAnticoagulantsNotes = v.findViewById(R.id.etAnticoagulantsNotes)
        etContagiousDiseasesNotes = v.findViewById(R.id.etContagiousDiseasesNotes)

        spinnerDiabeticType.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, diabeticTypes)
    }

    override fun prefill(history: PatientHistory?) {
        history ?: return
        etDoctorName.setText(history.doctorName ?: "")
        etDoctorPhone.setText(history.doctorPhone ?: "")
        etDoctorDiagnosis.setText(history.doctorDiagnosis ?: "")
        etMedication.setText(history.medication ?: "")
        etAllergies.setText(history.allergies ?: "")

        switchDiabetic.isChecked = history.isDiabetic
        spinnerDiabeticType.setSelection(diabeticTypes.indexOf(history.diabeticType ?: ""))
        etInsulinNotes.setText(history.insulinNotes ?: "")
        etPillsNotes.setText(history.pillsNotes ?: "")

        etOtherConditions.setText(history.otherConditions ?: "")
        etAnticoagulantsNotes.setText(history.anticoagulantsNotes ?: "")
        etContagiousDiseasesNotes.setText(history.contagiousDiseasesNotes ?: "")
    }

    override fun collectInto(aggr: PatientHistoryAggregator) {
        aggr.doctorName = etDoctorName.text.toString().trim().ifEmpty { null }
        aggr.doctorPhone = etDoctorPhone.text.toString().trim().ifEmpty { null }
        aggr.doctorDiagnosis = etDoctorDiagnosis.text.toString().trim().ifEmpty { null }
        aggr.medication = etMedication.text.toString().trim().ifEmpty { null }
        aggr.allergies = etAllergies.text.toString().trim().ifEmpty { null }

        aggr.isDiabetic = switchDiabetic.isChecked
        aggr.diabeticType = (spinnerDiabeticType.selectedItem as? String)?.ifEmpty { null }
        aggr.insulinNotes = etInsulinNotes.text.toString().trim().ifEmpty { null }
        aggr.pillsNotes = etPillsNotes.text.toString().trim().ifEmpty { null }

        aggr.otherConditions = etOtherConditions.text.toString().trim().ifEmpty { null }
        aggr.anticoagulantsNotes = etAnticoagulantsNotes.text.toString().trim().ifEmpty { null }
        aggr.contagiousDiseasesNotes = etContagiousDiseasesNotes.text.toString().trim().ifEmpty { null }
    }
}
