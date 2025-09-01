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

    private lateinit var cbLeftHyperkeratosis: CheckBox
    private lateinit var cbLeftHalluxValgus: CheckBox
    private lateinit var cbLeftWarts: CheckBox
    private lateinit var cbLeftDermatophytosis: CheckBox

    private lateinit var etLeftDorsalCalluses: EditText
    private lateinit var etLeftInterdigitalCalluses: EditText
    private lateinit var etLeftPlantarCalluses: EditText
    private lateinit var etLeftHammerToe: EditText
    private lateinit var etLeftOnychomycosis: EditText
    private lateinit var etLeftOnychocryptosis: EditText
    private lateinit var etLeftNailStatus: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history_left_foot, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        cbLeftHyperkeratosis = v.findViewById(R.id.cbLeftHyperkeratosis)
        cbLeftHalluxValgus = v.findViewById(R.id.cbLeftHalluxValgus)
        cbLeftWarts = v.findViewById(R.id.cbLeftWarts)
        cbLeftDermatophytosis = v.findViewById(R.id.cbLeftDermatophytosis)

        etLeftDorsalCalluses = v.findViewById(R.id.etLeftDorsalCalluses)
        etLeftInterdigitalCalluses = v.findViewById(R.id.etLeftInterdigitalCalluses)
        etLeftPlantarCalluses = v.findViewById(R.id.etLeftPlantarCalluses)
        etLeftHammerToe = v.findViewById(R.id.etLeftHammerToe)
        etLeftOnychomycosis = v.findViewById(R.id.etLeftOnychomycosis)
        etLeftOnychocryptosis = v.findViewById(R.id.etLeftOnychocryptosis)
        etLeftNailStatus = v.findViewById(R.id.etLeftNailStatus)
    }

    override fun prefill(history: PatientHistory?) {
        history ?: return
        cbLeftHyperkeratosis.isChecked = history.leftHyperkeratosis
        cbLeftHalluxValgus.isChecked = history.leftHalluxValgus
        cbLeftWarts.isChecked = history.leftWarts
        cbLeftDermatophytosis.isChecked = history.leftDermatophytosis

        etLeftDorsalCalluses.setText(history.leftDorsalCallusesNotes ?: "")
        etLeftInterdigitalCalluses.setText(history.leftInterdigitalCallusesNotes ?: "")
        etLeftPlantarCalluses.setText(history.leftPlantarCallusesNotes ?: "")
        etLeftHammerToe.setText(history.leftHammerToeNotes ?: "")
        etLeftOnychomycosis.setText(history.leftOnychomycosisNotes ?: "")
        etLeftOnychocryptosis.setText(history.leftOnychocryptosisNotes ?: "")
        etLeftNailStatus.setText(history.leftNailStatusNotes ?: "")
    }

    override fun collectInto(aggr: PatientHistoryAggregator) {
        aggr.leftHyperkeratosis = cbLeftHyperkeratosis.isChecked
        aggr.leftHalluxValgus = cbLeftHalluxValgus.isChecked
        aggr.leftWarts = cbLeftWarts.isChecked
        aggr.leftDermatophytosis = cbLeftDermatophytosis.isChecked

        aggr.leftDorsalCallusesNotes = etLeftDorsalCalluses.text.toString().trim().ifEmpty { null }
        aggr.leftInterdigitalCallusesNotes = etLeftInterdigitalCalluses.text.toString().trim().ifEmpty { null }
        aggr.leftPlantarCallusesNotes = etLeftPlantarCalluses.text.toString().trim().ifEmpty { null }
        aggr.leftHammerToeNotes = etLeftHammerToe.text.toString().trim().ifEmpty { null }
        aggr.leftOnychomycosisNotes = etLeftOnychomycosis.text.toString().trim().ifEmpty { null }
        aggr.leftOnychocryptosisNotes = etLeftOnychocryptosis.text.toString().trim().ifEmpty { null }
        aggr.leftNailStatusNotes = etLeftNailStatus.text.toString().trim().ifEmpty { null }
    }
}
