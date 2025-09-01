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

class HistoryLeftFootFragment : Fragment(), HistorySection {

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
        return inflater.inflate(R.layout.fragment_history_left_foot, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        cbHyperkeratosis = v.findViewById(R.id.cbLeftHyperkeratosis)
        cbHalluxValgus = v.findViewById(R.id.cbLeftHalluxValgus)
        cbWarts = v.findViewById(R.id.cbLeftWarts)
        cbDermatophytosis = v.findViewById(R.id.cbLeftDermatophytosis)

        etDorsalCalluses = v.findViewById(R.id.etLeftDorsalCalluses)
        etInterdigitalCalluses = v.findViewById(R.id.etLeftInterdigitalCalluses)
        etPlantarCalluses = v.findViewById(R.id.etLeftPlantarCalluses)
        etHammerToe = v.findViewById(R.id.etLeftHammerToe)
        etOnychomycosis = v.findViewById(R.id.etLeftOnychomycosis)
        etOnychocryptosis = v.findViewById(R.id.etLeftOnychocryptosis)
        etNailStatus = v.findViewById(R.id.etLeftNailStatus)
    }

    override fun prefill(history: PatientHistory?) {
        history ?: return
        cbHyperkeratosis.isChecked = history.leftHyperkeratosis
        cbHalluxValgus.isChecked = history.leftHalluxValgus
        cbWarts.isChecked = history.leftWarts
        cbDermatophytosis.isChecked = history.leftDermatophytosis
        etDorsalCalluses.setText(history.leftDorsalCallusesNotes ?: "")
        etInterdigitalCalluses.setText(history.leftInterdigitalCallusesNotes ?: "")
        etPlantarCalluses.setText(history.leftPlantarCallusesNotes ?: "")
        etHammerToe.setText(history.leftHammerToeNotes ?: "")
        etOnychomycosis.setText(history.leftOnychomycosisNotes ?: "")
        etOnychocryptosis.setText(history.leftOnychocryptosisNotes ?: "")
        etNailStatus.setText(history.leftNailStatusNotes ?: "")
    }

    override fun collectInto(aggr: PatientHistoryAggregator) {
        aggr.leftHyperkeratosis = cbHyperkeratosis.isChecked
        aggr.leftHalluxValgus = cbHalluxValgus.isChecked
        aggr.leftWarts = cbWarts.isChecked
        aggr.leftDermatophytosis = cbDermatophytosis.isChecked
        aggr.leftDorsalCallusesNotes = etDorsalCalluses.text.toString().trim().ifEmpty { null }
        aggr.leftInterdigitalCallusesNotes = etInterdigitalCalluses.text.toString().trim().ifEmpty { null }
        aggr.leftPlantarCallusesNotes = etPlantarCalluses.text.toString().trim().ifEmpty { null }
        aggr.leftHammerToeNotes = etHammerToe.text.toString().trim().ifEmpty { null }
        aggr.leftOnychomycosisNotes = etOnychomycosis.text.toString().trim().ifEmpty { null }
        aggr.leftOnychocryptosisNotes = etOnychocryptosis.text.toString().trim().ifEmpty { null }
        aggr.leftNailStatusNotes = etNailStatus.text.toString().trim().ifEmpty { null }
    }
}
