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

    private lateinit var cbCornDorsalLeft: CheckBox // ΝΕΟ
    private lateinit var cbCornPlantarLeft: CheckBox // ΝΕΟ

    private lateinit var etLeftDorsalCallusesNotes: EditText
    private lateinit var etLeftInterdigitalCallusesNotes: EditText
    private lateinit var etLeftPlantarCallusesNotes: EditText
    private lateinit var etLeftHammerToeNotes: EditText
    private lateinit var etLeftOnychomycosisNotes: EditText
    private lateinit var etLeftOnychocryptosisNotes: EditText
    private lateinit var etLeftNailStatusNotes: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_history_left_foot, container, false)

        cbLeftHyperkeratosis = v.findViewById(R.id.cbLeftHyperkeratosis)
        cbLeftHalluxValgus = v.findViewById(R.id.cbLeftHalluxValgus)
        cbLeftWarts = v.findViewById(R.id.cbLeftWarts)
        cbLeftDermatophytosis = v.findViewById(R.id.cbLeftDermatophytosis)

        cbCornDorsalLeft = v.findViewById(R.id.cbCornDorsalLeft)
        cbCornPlantarLeft = v.findViewById(R.id.cbCornPlantarLeft)

        etLeftDorsalCallusesNotes = v.findViewById(R.id.etLeftDorsalCallusesNotes)
        etLeftInterdigitalCallusesNotes = v.findViewById(R.id.etLeftInterdigitalCallusesNotes)
        etLeftPlantarCallusesNotes = v.findViewById(R.id.etLeftPlantarCallusesNotes)
        etLeftHammerToeNotes = v.findViewById(R.id.etLeftHammerToeNotes)
        etLeftOnychomycosisNotes = v.findViewById(R.id.etLeftOnychomycosisNotes)
        etLeftOnychocryptosisNotes = v.findViewById(R.id.etLeftOnychocryptosisNotes)
        etLeftNailStatusNotes = v.findViewById(R.id.etLeftNailStatusNotes)

        return v
    }

    override fun prefill(history: PatientHistory?) {
        if (history == null) return
        cbLeftHyperkeratosis.isChecked = history.leftHyperkeratosis
        cbLeftHalluxValgus.isChecked = history.leftHalluxValgus
        cbLeftWarts.isChecked = history.leftWarts
        cbLeftDermatophytosis.isChecked = history.leftDermatophytosis

        cbCornDorsalLeft.isChecked = history.cornDorsalLeft
        cbCornPlantarLeft.isChecked = history.cornPlantarLeft

        etLeftDorsalCallusesNotes.setText(history.leftDorsalCallusesNotes ?: "")
        etLeftInterdigitalCallusesNotes.setText(history.leftInterdigitalCallusesNotes ?: "")
        etLeftPlantarCallusesNotes.setText(history.leftPlantarCallusesNotes ?: "")
        etLeftHammerToeNotes.setText(history.leftHammerToeNotes ?: "")
        etLeftOnychomycosisNotes.setText(history.leftOnychomycosisNotes ?: "")
        etLeftOnychocryptosisNotes.setText(history.leftOnychocryptosisNotes ?: "")
        etLeftNailStatusNotes.setText(history.leftNailStatusNotes ?: "")
    }

    override fun collectInto(aggr: PatientHistoryAggregator) {
        aggr.leftHyperkeratosis = cbLeftHyperkeratosis.isChecked
        aggr.leftHalluxValgus = cbLeftHalluxValgus.isChecked
        aggr.leftWarts = cbLeftWarts.isChecked
        aggr.leftDermatophytosis = cbLeftDermatophytosis.isChecked

        aggr.cornDorsalLeft = cbCornDorsalLeft.isChecked
        aggr.cornPlantarLeft = cbCornPlantarLeft.isChecked

        aggr.leftDorsalCallusesNotes = etLeftDorsalCallusesNotes.text?.toString()
        aggr.leftInterdigitalCallusesNotes = etLeftInterdigitalCallusesNotes.text?.toString()
        aggr.leftPlantarCallusesNotes = etLeftPlantarCallusesNotes.text?.toString()
        aggr.leftHammerToeNotes = etLeftHammerToeNotes.text?.toString()
        aggr.leftOnychomycosisNotes = etLeftOnychomycosisNotes.text?.toString()
        aggr.leftOnychocryptosisNotes = etLeftOnychocryptosisNotes.text?.toString()
        aggr.leftNailStatusNotes = etLeftNailStatusNotes.text?.toString()
    }
}
