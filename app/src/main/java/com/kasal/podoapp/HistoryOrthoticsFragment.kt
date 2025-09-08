package com.kasal.podoapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PatientHistory

class HistoryOrthoticsFragment : Fragment(), HistorySection {

    private lateinit var switchHasSplint: Switch
    private lateinit var etSplintType: EditText
    private lateinit var etSplintNotes: EditText

    private lateinit var spinnerOrthoticType: Spinner
    private lateinit var etOrthoticNumber: EditText
    private lateinit var etOrthoticNotes: EditText

    private val orthoticTypes = arrayOf("—", "NONE", "STOCK", "CUSTOM")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_history_orthotics, container, false)

        switchHasSplint = v.findViewById(R.id.switchHasSplint)
        etSplintType = v.findViewById(R.id.etSplintType)
        etSplintNotes = v.findViewById(R.id.etSplintNotes)

        spinnerOrthoticType = v.findViewById(R.id.spinnerOrthoticType)
        etOrthoticNumber = v.findViewById(R.id.etOrthoticNumber)
        etOrthoticNotes = v.findViewById(R.id.etOrthoticNotes)

        spinnerOrthoticType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            orthoticTypes
        )

        switchHasSplint.setOnCheckedChangeListener { _, checked -> enableSplint(checked) }
        return v
    }

    private fun enableSplint(enabled: Boolean) {
        etSplintType.isEnabled = enabled
        etSplintNotes.isEnabled = enabled
        if (!enabled) {
            etSplintType.setText("")
            // Αν θες και notes clear:
            // etSplintNotes.setText("")
        }
    }

    override fun prefill(history: PatientHistory?) {
        if (history == null) return
        switchHasSplint.isChecked = history.hasSplint
        etSplintType.setText(history.splintType ?: "")
        etSplintNotes.setText(history.splintNotes ?: "")
        enableSplint(history.hasSplint)

        val idx = orthoticTypes.indexOf(history.orthoticType ?: "—")
        spinnerOrthoticType.setSelection(if (idx >= 0) idx else 0)

        etOrthoticNumber.setText(history.orthoticNumber ?: "")
        etOrthoticNotes.setText(history.orthoticNotes ?: "")
    }

    override fun collectInto(aggr: PatientHistoryAggregator) {
        aggr.hasSplint = switchHasSplint.isChecked
        aggr.splintType = etSplintType.text?.toString()
        aggr.splintNotes = etSplintNotes.text?.toString()

        val sel = orthoticTypes[spinnerOrthoticType.selectedItemPosition]
        aggr.orthoticType = if (sel == "—") null else sel
        aggr.orthoticNumber = etOrthoticNumber.text?.toString()
        aggr.orthoticNotes = etOrthoticNotes.text?.toString()
    }
}
