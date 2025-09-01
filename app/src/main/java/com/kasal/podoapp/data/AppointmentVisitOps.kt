package com.kasal.podoapp.data

import android.content.Context
import androidx.room.withTransaction

object AppointmentVisitOps {

    const val STATUS_DONE = "DONE" // label για “πραγματοποιήθηκε”

    /**
     * Μετατρέπει ένα ραντεβού σε επίσκεψη σε μία Room transaction.
     * @param actualDateTime αν είναι null, χρησιμοποιούμε το dateTime του ραντεβού
     * @return το id της νέας επίσκεψης
     */
    suspend fun convertAppointmentToVisit(
        context: Context,
        appointmentId: Int,
        actualDateTime: Long? = null
    ): Long {
        val db = PodologiaDatabase.getDatabase(context)
        return db.withTransaction {
            val appt = db.appointmentDao().getById(appointmentId)
                ?: error("Appointment $appointmentId δεν βρέθηκε")

            val visitId = db.visitDao().insert(
                Visit(
                    id = 0,
                    patientId = appt.patientId,
                    appointmentId = appt.id,
                    dateTime = actualDateTime ?: appt.dateTime,
                    notes = appt.notes,
                    charge = appt.charge,
                    treatment = appt.treatment
                )
            )
            db.appointmentDao().updateStatus(appointmentId, STATUS_DONE)
            visitId
        }
    }
}
