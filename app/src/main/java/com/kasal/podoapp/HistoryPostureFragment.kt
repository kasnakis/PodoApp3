package com.kasal.podoapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PatientHistory

class HistoryPostureFragment : Fragment(), HistorySection {

    private lateinit var cbMetatarsalDrop: CheckBox // legacy συνολικό
    private lateinit var cbMetatarsalDropLeft: CheckBox
    private lateinit var cbMetatarsalDropRight: CheckBox

    private lateinit var cbValgus: CheckBox
    private lateinit var cbVarus: CheckBox
    private lateinit var cbEquinus: CheckBox
    private lateinit var cbCavus: CheckBox
    private lateinit var cbFlatfoot: CheckBox
    private lateinit var cbPronation: CheckBox
    private lateinit var cbSupination: CheckBox

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_history_posture, container, false)
        cbMetatarsalDrop = v.findViewById(R.id.cbMetatarsalDrop)
        cbMetatarsalDropLeft = v.findViewById(R.id.cbMetatarsalDropLeft)
        cbMetatarsalDropRight = v.findViewById(R.id.cbMetatarsalDropRight)

        cbValgus = v.findViewById(R.id.cbValgus)
        cbVarus = v.findViewById(R.id.cbVarus)
        cbEquinus = v.findViewById(R.id.cbEquinus)
        cbCavus = v.findViewById(R.id.cbCavus)
        cbFlatfoot = v.findViewById(R.id.cbFlatfoot)
        cbPronation = v.findViewById(R.id.cbPronation)
        cbSupination = v.findViewById(R.id.cbSupination)

        cbMetatarsalDropLeft.setOnCheckedChangeListener { _, _ -> syncMeta() }
        cbMetatarsalDropRight.setOnCheckedChangeListener { _, _ -> syncMeta() }
        cbMetatarsalDrop.setOnCheckedChangeListener { _, checked ->
            if (!checked) {
                cbMetatarsalDropLeft.isChecked = false
                cbMetatarsalDropRight.isChecked = false
            }
        }
        return v
    }

    private fun syncMeta() {
        cbMetatarsalDrop.isChecked = cbMetatarsalDropLeft.isChecked || cbMetatarsalDropRight.isChecked
    }

    override fun prefill(history: PatientHistory?) {
        if (history == null) return
        cbMetatarsalDrop.isChecked = history.metatarsalDrop
        cbMetatarsalDropLeft.isChecked = history.metatarsalDropLeft
        cbMetatarsalDropRight.isChecked = history.metatarsalDropRight

        cbValgus.isChecked = history.valgus
        cbVarus.isChecked = history.varus
        cbEquinus.isChecked = history.equinus
        cbCavus.isChecked = history.cavus
        cbFlatfoot.isChecked = history.flatfoot
        cbPronation.isChecked = history.pronation
        cbSupination.isChecked = history.supination

        syncMeta()
    }

    override fun collectInto(aggr: PatientHistoryAggregator) {
        aggr.metatarsalDrop = cbMetatarsalDrop.isChecked
        aggr.metatarsalDropLeft = cbMetatarsalDropLeft.isChecked
        aggr.metatarsalDropRight = cbMetatarsalDropRight.isChecked

        aggr.valgus = cbValgus.isChecked
        aggr.varus = cbVarus.isChecked
        aggr.equinus = cbEquinus.isChecked
        aggr.cavus = cbCavus.isChecked
        aggr.flatfoot = cbFlatfoot.isChecked
        aggr.pronation = cbPronation.isChecked
        aggr.supination = cbSupination.isChecked
    }
}
