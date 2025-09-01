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

    private lateinit var etRightDorsalCalluses: EditText
    private lateinit var etRightInterdigitalCalluses: EditText
    private lateinit var etRightPlantarCalluses: EditText
    private lateinit var etRightHammerToe: EditText
    private lateinit var etRightOnychomycosis: EditText
    private lateinit var etRightOnychocryptosis: EditText
    private lateinit var etRightNailStatus: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history_right_foot, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        cbRightHyperkeratosis = v.findViewById(R.id.cbRightHyperkeratosis)
        cbRightHalluxValgus = v.findViewById(R.id.cbRightHalluxValgus)
        cbRightWarts = v.findViewById(R.id.cbRightWarts)
        cbRightDermatophytosis = v.findViewById(R.id.cbRightDermatophytosis)

        etRightDorsalCalluses = v.findViewById(R.id.etRightDorsalCalluses)
        etRightInterdigitalCalluses = v.findViewById(R.id.etRightInterdigitalCalluses)
        etRightPlantarCalluses = v.findViewById(R.id.etRightPlantarCalluses)
        etRightHammerToe = v.findViewById(R.id.etRightHammerToe)
        etRightOnychomycosis = v.findViewById(R.id.etRightOnychomycosis)
        etRightOnychocryptosis = v.findViewById(R.id.etRightOnychocryptosis)
        etRightNailStatus = v.findViewById(R.id.etRightNailStatus)
    }

    override fun prefill(history: PatientHistory?) {
        history ?: return
        cbRightHyperkeratosis.isChecked = history.rightHyperkeratosis
        cbRightHalluxValgus.isChecked = history.rightHalluxValgus
        cbRightWarts.isChecked = history.rightWarts
        cbRightDermatophytosis.isChecked = history.rightDermatophytosis

        etRightDorsalCalluses.setText(history.rightDorsalCallusesNotes ?: "")
        etRightInterdigitalCalluses.setText(history.rightInterdigitalCallusesNotes ?: "")
        etRightPlantarCalluses.setText(history.rightPlantarCallusesNotes ?: "")
        etRightHammerToe.setText(history.rightHammerToeNotes ?: "")
        etRightOnychomycosis.setText(history.rightOnychomycosisNotes ?: "")
        etRightOnychocryptosis.setText(history.rightOnychocryptosisNotes ?: "")
        etRightNailStatus.setText(history.rightNailStatusNotes ?: "")
    }

    override fun collectInto(aggr: PatientHistoryAggregator) {
        aggr.rightHyperkeratosis = cbRightHyperkeratosis.isChecked
        aggr.rightHalluxValgus = cbRightHalluxValgus.isChecked
        aggr.rightWarts = cbRightWarts.isChecked
        aggr.rightDermatophytosis = cbRightDermatophytosis.isChecked

        aggr.rightDorsalCallusesNotes = etRightDorsalCalluses.text.toString().trim().ifEmpty { null }
        aggr.rightInterdigitalCallusesNotes = etRightInterdigitalCalluses.text.toString().trim().ifEmpty { null }
        aggr.rightPlantarCallusesNotes = etRightPlantarCalluses.text.toString().trim().ifEmpty { null }
        aggr.rightHammerToeNotes = etRightHammerToe.text.toString().trim().ifEmpty { null }
        aggr.rightOnychomycosisNotes = etRightOnychomycosis.text.toString().trim().ifEmpty { null }
        aggr.rightOnychocryptosisNotes = etRightOnychocryptosis.text.toString().trim().ifEmpty { null }
        aggr.rightNailStatusNotes = etRightNailStatus.text.toString().trim().ifEmpty { null }
    }
}
