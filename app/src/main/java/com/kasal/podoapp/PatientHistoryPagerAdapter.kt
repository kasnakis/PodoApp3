package com.kasal.podoapp.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kasal.podoapp.data.PatientHistory

class PatientHistoryPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments = LinkedHashMap<Int, Fragment>()

    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {
        val f: Fragment = when (position) {
            0 -> HistoryMedicalFragment()
            1 -> HistoryPostureFragment()
            2 -> HistoryLeftFootFragment()
            3 -> HistoryRightFootFragment()
            4 -> HistoryVascularFragment()
            else -> HistoryOrthoticsFragment()
        }
        fragments[position] = f
        return f
    }

    fun prefillAll(history: PatientHistory?) {
        fragments.values.forEach { (it as? HistorySection)?.prefill(history) }
    }

    fun collectAllInto(aggr: PatientHistoryAggregator) {
        fragments.values.forEach { (it as? HistorySection)?.collectInto(aggr) }
    }
}
