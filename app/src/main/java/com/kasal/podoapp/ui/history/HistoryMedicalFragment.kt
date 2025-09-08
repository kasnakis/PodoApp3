package com.kasal.podoapp.ui.history

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.kasal.podoapp.R

/**
 * Tab: Ιατρικά / Στοιχεία Ιατρών & Αγωγής + Διαβήτης + Άλλες παθήσεις
 * Προβάλλει/ενημερώνει τον PatientHistoryAggregator.
 *
 * Απλή σύμβαση: το Activity-γονέας υλοποιεί το PatientHistoryHost
 * ώστε να πάρουμε το shared aggregator.
 */
class HistoryMedicalFragment : Fragment() {

    interface PatientHistoryHost {
        fun getHistoryAggregator(): PatientHistoryAggregator
    }

    private val host: PatientHistoryHost?
        get() = activity as? PatientHistoryHost

    private lateinit var etDoctorName: EditText
    private lateinit var etDoctorPhone: EditText
    private lateinit var etDiagnosis: EditText
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
    private lateinit var etContagiousNotes: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_history_medical, container, false)

        // Bind views
        etDoctorName = v.findViewById(R.id.etDoctorName)
        etDoctorPhone = v.findViewById(R.id.etDoctorPhone)
        etDiagnosis = v.findViewById(R.id.etDoctorDiagnosis)
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
        etContagiousNotes = v.findViewById(R.id.etContagiousNotes)

        // Διαβητικός type spinner (τύποι)
        val types = arrayOf("—", "TYPE_1", "TYPE_2")
        spinnerDiabeticType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            types
        )

        // Prefill από aggregator
        host?.getHistoryAggregator()?.let { agg ->
            etDoctorName.setText(agg.doctorName ?: "")
            etDoctorPhone.setText(agg.doctorPhone ?: "")
            etDiagnosis.setText(agg.doctorDiagnosis ?: "")
            etMedication.setText(agg.medication ?: "")
            etAllergies.setText(agg.allergies ?: "")

            switchDiabetic.isChecked = agg.isDiabetic
            spinnerDiabeticType.setSelection(
                types.indexOf(agg.diabeticType ?: "—").coerceAtLeast(0)
            )
            etDiabetesSince.setText(agg.diabetesSinceNotes ?: "")
            etInsulinNotes.setText(agg.insulinNotes ?: "")
            etPillsNotes.setText(agg.pillsNotes ?: "")

            switchHasOtherConditions.isChecked = agg.hasOtherConditions
            etOtherConditionsNotes.setText(agg.otherConditionsNotes ?: "")
            etAnticoagulantsNotes.setText(agg.anticoagulantsNotes ?: "")
            etContagiousNotes.setText(agg.contagiousDiseasesNotes ?: "")

            enableOtherConditions(agg.hasOtherConditions)
            enableDiabetes(agg.isDiabetic)
        }

        // Listeners -> ενημέρωση aggregator
        etDoctorName.addTextChangedListener { host?.getHistoryAggregator()?.doctorName = it?.toString() }
        etDoctorPhone.addTextChangedListener { host?.getHistoryAggregator()?.doctorPhone = it?.toString() }
        etDiagnosis.addTextChangedListener { host?.getHistoryAggregator()?.doctorDiagnosis = it?.toString() }
        etMedication.addTextChangedListener { host?.getHistoryAggregator()?.medication = it?.toString() }
        etAllergies.addTextChangedListener { host?.getHistoryAggregator()?.allergies = it?.toString() }

        switchDiabetic.setOnCheckedChangeListener { _, checked ->
            host?.getHistoryAggregator()?.isDiabetic = checked
            enableDiabetes(checked)
        }

        spinnerDiabeticType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val sel = types[position]
                host?.getHistoryAggregator()?.diabeticType = if (sel == "—") null else sel
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { /* no-op */ }
        }

        etDiabetesSince.addTextChangedListener { host?.getHistoryAggregator()?.diabetesSinceNotes = it?.toString() }
        etInsulinNotes.addTextChangedListener { host?.getHistoryAggregator()?.insulinNotes = it?.toString() }
        etPillsNotes.addTextChangedListener { host?.getHistoryAggregator()?.pillsNotes = it?.toString() }

        switchHasOtherConditions.setOnCheckedChangeListener { _, checked ->
            host?.getHistoryAggregator()?.hasOtherConditions = checked
            if (!checked) etOtherConditionsNotes.setText("")
            enableOtherConditions(checked)
        }

        etOtherConditionsNotes.addTextChangedListener { host?.getHistoryAggregator()?.otherConditionsNotes = it?.toString() }
        etAnticoagulantsNotes.addTextChangedListener { host?.getHistoryAggregator()?.anticoagulantsNotes = it?.toString() }
        etContagiousNotes.addTextChangedListener { host?.getHistoryAggregator()?.contagiousDiseasesNotes = it?.toString() }

        return v
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
}
