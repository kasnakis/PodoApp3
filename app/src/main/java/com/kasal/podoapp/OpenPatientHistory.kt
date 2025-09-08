package com.kasal.podoapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kasal.podoapp.ui.PatientHistoryTabsActivity

// Από Activity
fun AppCompatActivity.openPatientHistory(patientId: Int) {
    val i = Intent(this, PatientHistoryTabsActivity::class.java)
    i.putExtra("patientId", patientId)
    startActivity(i)
}

// Από Fragment
fun Fragment.openPatientHistory(patientId: Int) {
    val ctx = requireContext()
    val i = Intent(ctx, PatientHistoryTabsActivity::class.java)
    i.putExtra("patientId", patientId)
    startActivity(i)
}

// Από Context (π.χ. Adapter/ViewHolder)
fun Context.openPatientHistory(patientId: Int) {
    val i = Intent(this, PatientHistoryTabsActivity::class.java).apply {
        putExtra("patientId", patientId)
    }
    startActivity(i)
}
