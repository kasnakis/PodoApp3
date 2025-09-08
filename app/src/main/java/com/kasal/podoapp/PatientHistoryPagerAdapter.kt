package com.kasal.podoapp.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PatientHistoryPagerAdapter(
    fa: FragmentActivity
) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = 6 // Ιατρικά, Στάση, Αριστερό, Δεξί, Οίδημα/Κιρσοί, Ορθωτικά

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> HistoryMedicalFragment()
        1 -> HistoryPostureFragment()
        2 -> HistoryLeftFootFragment()
        3 -> HistoryRightFootFragment()
        4 -> HistoryVascularFragment() // κράτα το υπάρχον σου
        5 -> HistoryOrthoticsFragment()
        else -> HistoryMedicalFragment()
    }
}
