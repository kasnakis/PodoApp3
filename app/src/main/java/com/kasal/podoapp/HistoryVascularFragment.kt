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
    private lateinit var cbedemaRight: CheckBox
    private lateinit var cbVarDorsalLeft: CheckBox
    private lateinit var cbVarDorsalRight: CheckBox
    private lateinit var cbVarPlantarLeft: CheckBox
    private lateinit var cbVarPlantarRight: CheckBox

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history_vascular, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        cbEdemaLeft = v.findViewById(R.id.cbEdemaLeft)
        cbedemaRight = v.findViewById(R.id.cbedemaRight)
        cbVarDorsalLeft = v.findViewById(R.id.cbVaricoseDorsalLeft)
        cbVarDorsalRight = v.findViewById(R.id.cbvaricoseDorsalRight)
        cbVarPlantarLeft = v.findViewById(R.id.cbVaricosePlantarLeft)
        cbVarPlantarRight = v.findViewById(R.id.cbvaricosePlantarRight)
    }

    override fun prefill(history: PatientHistory?) {
        history ?: return
        cbEdemaLeft.isChecked = history.edemaLeft
        cbedemaRight.isChecked = history.edemaRight
        cbVarDorsalLeft.isChecked = history.varicoseDorsalLeft
        cbVarDorsalRight.isChecked = history.varicoseDorsalRight
        cbVarPlantarLeft.isChecked = history.varicosePlantarLeft
        cbVarPlantarRight.isChecked = history.varicosePlantarRight
    }

    override fun collectInto(aggr: PatientHistoryAggregator) {
        aggr.edemaLeft = cbEdemaLeft.isChecked
        aggr.edemaRight = cbedemaRight.isChecked
        aggr.varicoseDorsalLeft = cbVarDorsalLeft.isChecked
        aggr.varicoseDorsalRight = cbVarDorsalRight.isChecked
        aggr.varicosePlantarLeft = cbVarPlantarLeft.isChecked
        aggr.varicosePlantarRight = cbVarPlantarRight.isChecked
    }
}
