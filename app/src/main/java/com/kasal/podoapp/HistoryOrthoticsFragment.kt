package com.kasal.podoapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PatientHistory

class HistoryOrthoticsFragment : Fragment(), HistorySection {

    private lateinit var etSplintNotes: EditText
    private lateinit var spinnerOrthoticType: Spinner
    private val orthoticTypes = arrayOf("NONE", "STOCK", "CUSTOM")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history_orthotics, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        etSplintNotes = v.findViewById(R.id.etSplintNotes)
        spinnerOrthoticType = v.findViewById(R.id.spinnerOrthoticType)
        spinnerOrthoticType.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, orthoticTypes)
    }

    override fun prefill(history: PatientHistory?) {
        history ?: return
        etSplintNotes.setText(history.splintNotes ?: "")
        spinnerOrthoticType.setSelection(orthoticTypes.indexOf(history.orthoticType ?: "NONE"))
    }

    override fun collectInto(aggr: PatientHistoryAggregator) {
        aggr.splintNotes = etSplintNotes.text.toString().trim().ifEmpty { null }
        aggr.orthoticType = orthoticTypes[spinnerOrthoticType.selectedItemPosition]
    }
}
