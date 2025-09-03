package com.kasal.podoapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kasal.podoapp.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Μινιμαλιστική λίστα ενεργών ενεργειών (ταιριάζει με το καθαρό layout)
        val actions: List<Pair<Int, List<String>>> = listOf(
            R.id.btnAddPatient          to fqcn("AddPatientActivity"),
            R.id.btnPatientList         to fqcn("PatientListActivity"),
            R.id.btnNewAppointment      to fqcn("NewAppointmentActivity"),
            R.id.btnAppointmentCalendar to fqcn("AppointmentCalendarActivity")
        )

        actions.forEach { (viewId, candidates) ->
            bindOrHide(viewId, candidates)
        }
    }

    /** Προτιμάμε ui.* και ως fallback root package. */
    private fun fqcn(simple: String): List<String> = listOf(
        "com.kasal.podoapp.ui.$simple",
        "com.kasal.podoapp.$simple"
    )

    /** Αν υπάρχει Activity, δένει onClick, αλλιώς GONE. */
    private fun bindOrHide(viewId: Int, candidates: List<String>) {
        val v = findViewById<View?>(viewId) ?: return
        val clazz = resolveFirstExistingActivity(candidates) ?: run {
            v.visibility = View.GONE
            return
        }
        val intent = Intent().setClassName(this, clazz.name)
        val canResolve = packageManager.resolveActivity(intent, 0) != null
        if (!canResolve) {
            v.visibility = View.GONE
            return
        }
        v.visibility = View.VISIBLE
        v.setOnClickListener { startActivity(intent) }
    }

    /** Βρες την πρώτη Activity κλάση που υπάρχει. */
    private fun resolveFirstExistingActivity(candidates: List<String>): Class<out Activity>? {
        for (name in candidates) {
            try {
                val c = Class.forName(name)
                if (Activity::class.java.isAssignableFrom(c)) {
                    @Suppress("UNCHECKED_CAST")
                    return c as Class<out Activity>
                }
            } catch (_: ClassNotFoundException) { /* try next */ }
        }
        return null
    }
}
