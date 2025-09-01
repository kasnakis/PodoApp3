package com.kasal.podoapp.ui

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PatientHistory

class HistoryOrthoticsFragment : Fragment(), HistorySection {

    private lateinit var etSplint: EditText
    private lateinit var spOrthotic: Spinner
    private val types = arrayOf("NONE", "STOCK", "CUSTOM")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history_orthotics, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        etSplint = v.findViewById(R.id.etSplintNotes)
        spOrthotic = v.findViewById(R.id.spinnerOrthoticType)
        spOrthotic.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, types)
    }

    override fun prefill(history: PatientHistory?) {
        history ?: return
        etSplint.setText(history.splintNotes ?: "")
        spOrthotic.setSelection(types.indexOf(history.orthoticType ?: "NONE"))
    }

    override fun collectInto(aggr: PatientHistoryAggregator) {
        aggr.splintNotes = etSplint.text.toString().trim().ifEmpty { null }
        aggr.orthoticType = types[spOrthotic.selectedItemPosition]
    }
}
