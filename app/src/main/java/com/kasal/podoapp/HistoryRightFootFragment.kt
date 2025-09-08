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

    private lateinit var cbRightHyperkeratosis: CheckBox
    private lateinit var cbRightHalluxValgus: CheckBox
    private lateinit var cbRightWarts: CheckBox
    private lateinit var cbRightDermatophytosis: CheckBox

    private lateinit var cbCornDorsalRight: CheckBox // ΝΕΟ
    private lateinit var cbCornPlantarRight: CheckBox // ΝΕΟ

    private lateinit var etRightDorsalCallusesNotes: EditText
    private lateinit var etRightInterdigitalCallusesNotes: EditText
    private lateinit var etRightPlantarCallusesNotes: EditText
    private lateinit var etRightHammerToeNotes: EditText
    private lateinit var etRightOnychomycosisNotes: EditText
    private lateinit var etRightOnychocryptosisNotes: EditText
    private lateinit var etRightNailStatusNotes: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_history_right_foot, container, false)

        cbRightHyperkeratosis = v.findViewById(R.id.cbRightHyperkeratosis)
        cbRightHalluxValgus = v.findViewById(R.id.cbRightHalluxValgus)
        cbRightWarts = v.findViewById(R.id.cbRightWarts)
        cbRightDermatophytosis = v.findViewById(R.id.cbRightDermatophytosis)

        cbCornDorsalRight = v.findViewById(R.id.cbCornDorsalRight)
        cbCornPlantarRight = v.findViewById(R.id.cbCornPlantarRight)

        etRightDorsalCallusesNotes = v.findViewById(R.id.etRightDorsalCallusesNotes)
        etRightInterdigitalCallusesNotes = v.findViewById(R.id.etRightInterdigitalCallusesNotes)
        etRightPlantarCallusesNotes = v.findViewById(R.id.etRightPlantarCallusesNotes)
        etRightHammerToeNotes = v.findViewById(R.id.etRightHammerToeNotes)
        etRightOnychomycosisNotes = v.findViewById(R.id.etRightOnychomycosisNotes)
        etRightOnychocryptosisNotes = v.findViewById(R.id.etRightOnychocryptosisNotes)
        etRightNailStatusNotes = v.findViewById(R.id.etRightNailStatusNotes)

        return v
    }

    override fun prefill(history: PatientHistory?) {
        if (history == null) return
        cbRightHyperkeratosis.isChecked = history.rightHyperkeratosis
        cbRightHalluxValgus.isChecked = history.rightHalluxValgus
        cbRightWarts.isChecked = history.rightWarts
        cbRightDermatophytosis.isChecked = history.rightDermatophytosis

        cbCornDorsalRight.isChecked = history.cornDorsalRight
        cbCornPlantarRight.isChecked = history.cornPlantarRight

        etRightDorsalCallusesNotes.setText(history.rightDorsalCallusesNotes ?: "")
        etRightInterdigitalCallusesNotes.setText(history.rightInterdigitalCallusesNotes ?: "")
        etRightPlantarCallusesNotes.setText(history.rightPlantarCallusesNotes ?: "")
        etRightHammerToeNotes.setText(history.rightHammerToeNotes ?: "")
        etRightOnychomycosisNotes.setText(history.rightOnychomycosisNotes ?: "")
        etRightOnychocryptosisNotes.setText(history.rightOnychocryptosisNotes ?: "")
        etRightNailStatusNotes.setText(history.rightNailStatusNotes ?: "")
    }

    override fun collectInto(aggr: PatientHistoryAggregator) {
        aggr.rightHyperkeratosis = cbRightHyperkeratosis.isChecked
        aggr.rightHalluxValgus = cbRightHalluxValgus.isChecked
        aggr.rightWarts = cbRightWarts.isChecked
        aggr.rightDermatophytosis = cbRightDermatophytosis.isChecked

        aggr.cornDorsalRight = cbCornDorsalRight.isChecked
        aggr.cornPlantarRight = cbCornPlantarRight.isChecked

        aggr.rightDorsalCallusesNotes = etRightDorsalCallusesNotes.text?.toString()
        aggr.rightInterdigitalCallusesNotes = etRightInterdigitalCallusesNotes.text?.toString()
        aggr.rightPlantarCallusesNotes = etRightPlantarCallusesNotes.text?.toString()
        aggr.rightHammerToeNotes = etRightHammerToeNotes.text?.toString()
        aggr.rightOnychomycosisNotes = etRightOnychomycosisNotes.text?.toString()
        aggr.rightOnychocryptosisNotes = etRightOnychocryptosisNotes.text?.toString()
        aggr.rightNailStatusNotes = etRightNailStatusNotes.text?.toString()
    }
}
