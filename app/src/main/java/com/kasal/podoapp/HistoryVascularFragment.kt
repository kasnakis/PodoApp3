package com.kasal.podoapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PatientHistory

class HistoryVascularFragment : Fragment(), HistorySection {

    private lateinit var cbEdemaLeft: CheckBox
    private lateinit var cbEdemaRight: CheckBox
    private lateinit var cbVaricoseDorsalLeft: CheckBox
    private lateinit var cbVaricoseDorsalRight: CheckBox
    private lateinit var cbVaricosePlantarLeft: CheckBox
    private lateinit var cbVaricosePlantarRight: CheckBox

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history_vascular, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        cbEdemaLeft = v.findViewById(R.id.cbEdemaLeft)
        cbEdemaRight = v.findViewById(R.id.cbEdemaRight)
        cbVaricoseDorsalLeft = v.findViewById(R.id.cbVaricoseDorsalLeft)
        cbVaricoseDorsalRight = v.findViewById(R.id.cbVaricoseDorsalRight)
        cbVaricosePlantarLeft = v.findViewById(R.id.cbVaricosePlantarLeft)
        cbVaricosePlantarRight = v.findViewById(R.id.cbVaricosePlantarRight)
    }

    override fun prefill(history: PatientHistory?) {
        history ?: return
        cbEdemaLeft.isChecked = history.edemaLeft
        cbEdemaRight.isChecked = history.edemaRight
        cbVaricoseDorsalLeft.isChecked = history.varicoseDorsalLeft
        cbVaricoseDorsalRight.isChecked = history.varicoseDorsalRight
        cbVaricosePlantarLeft.isChecked = history.varicosePlantarLeft
        cbVaricosePlantarRight.isChecked = history.varicosePlantarRight
    }

    override fun collectInto(aggr: PatientHistoryAggregator) {
        aggr.edemaLeft = cbEdemaLeft.isChecked
        aggr.edemaRight = cbEdemaRight.isChecked
        aggr.varicoseDorsalLeft = cbVaricoseDorsalLeft.isChecked
        aggr.varicoseDorsalRight = cbVaricoseDorsalRight.isChecked
        aggr.varicosePlantarLeft = cbVaricosePlantarLeft.isChecked
        aggr.varicosePlantarRight = cbVaricosePlantarRight.isChecked
    }
}
