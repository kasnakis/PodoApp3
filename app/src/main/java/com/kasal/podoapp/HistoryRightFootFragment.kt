package com.kasal.podoapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PatientHistory

class HistoryRightFootFragment : Fragment(), HistorySection {

    private lateinit var cbHyperkeratosis: CheckBox
    private lateinit var cbHalluxValgus: CheckBox
    private lateinit var cbWarts: CheckBox
    private lateinit var cbDermatophytosis: CheckBox

    private lateinit var etDorsalCalluses: EditText
    private lateinit var etInterdigitalCalluses: EditText
    private lateinit var etPlantarCalluses: EditText
    private lateinit var etHammerToe: EditText
    private lateinit var etOnychomycosis: EditText
    private lateinit var etOnychocryptosis: EditText
    private lateinit var etNailStatus: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history_right_foot, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        cbHyperkeratosis = v.findViewById(R.id.cbRightHyperkeratosis)
        cbHalluxValgus = v.findViewById(R.id.cbRightHalluxValgus)
        cbWarts = v.findViewById(R.id.cbRightWarts)
        cbDermatophytosis = v.findViewById(R.id.cbRightDermatophytosis)

        etDorsalCalluses = v.findViewById(R.id.etRightDorsalCalluses)
        etInterdigitalCalluses = v.findViewById(R.id.etRightInterdigitalCalluses)
        etPlantarCalluses = v.findViewById(R.id.etRightPlantarCalluses)
        etHammerToe = v.findViewById(R.id.etRightHammerToe)
        etOnychomycosis = v.findViewById(R.id.etRightOnychomycosis)
        etOnychocryptosis = v.findViewById(R.id.etRightOnychocryptosis)
        etNailStatus = v.findViewById(R.id.etRightNailStatus)
    }

    override fun prefill(history: PatientHistory?) {
        history ?: return
        cbHyperkeratosis.isChecked = history.rightHyperkeratosis
        cbHalluxValgus.isChecked = history.rightHalluxValgus
        cbWarts.isChecked = history.rightWarts
        cbDermatophytosis.isChecked = history.rightDermatophytosis
        etDorsalCalluses.setText(history.rightDorsalCallusesNotes ?: "")
        etInterdigitalCalluses.setText(history.rightInterdigitalCallusesNotes ?: "")
        etPlantarCalluses.setText(history.rightPlantarCallusesNotes ?: "")
        etHammerToe.setText(history.rightHammerToeNotes ?: "")
        etOnychomycosis.setText(history.rightOnychomycosisNotes ?: "")
        etOnychocryptosis.setText(history.rightOnychocryptosisNotes ?: "")
        etNailStatus.setText(history.rightNailStatusNotes ?: "")
    }

    override fun collectInto(aggr: PatientHistoryAggregator) {
        aggr.rightHyperkeratosis = cbHyperkeratosis.isChecked
        aggr.rightHalluxValgus = cbHalluxValgus.isChecked
        aggr.rightWarts = cbWarts.isChecked
        aggr.rightDermatophytosis = cbDermatophytosis.isChecked
        aggr.rightDorsalCallusesNotes = etDorsalCalluses.text.toString().trim().ifEmpty { null }
        aggr.rightInterdigitalCallusesNotes = etInterdigitalCalluses.text.toString().trim().ifEmpty { null }
        aggr.rightPlantarCallusesNotes = etPlantarCalluses.text.toString().trim().ifEmpty { null }
        aggr.rightHammerToeNotes = etHammerToe.text.toString().trim().ifEmpty { null }
        aggr.rightOnychomycosisNotes = etOnychomycosis.text.toString().trim().ifEmpty { null }
        aggr.rightOnychocryptosisNotes = etOnychocryptosis.text.toString().trim().ifEmpty { null }
        aggr.rightNailStatusNotes = etNailStatus.text.toString().trim().ifEmpty { null }
    }
}
