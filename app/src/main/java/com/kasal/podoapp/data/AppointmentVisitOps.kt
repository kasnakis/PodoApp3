package com.kasal.podoapp.data

import androidx.room.withTransaction

object AppointmentVisitOps {

    /**
     * Μετατρέπει ένα Appointment σε Visit και μαρκάρει το ραντεβού ως COMPLETED.
     * Επιστρέφει το visitId.
     *
     * Συμβατό με Visit(
     *   patientId: Int,
     *   appointmentId: Int,
     *   dateTime: Long,
     *   notes: String?,
     *   charge: <τύπος σου>,
     *   treatment: String?
     * )
     */
    suspend fun convertAppointmentToVisit(
        db: PodologiaDatabase,
        appt: Appointment
    ): Long {
        return db.withTransaction {
            val visit = Visit(
                patientId = appt.patientId,
                appointmentId = appt.id,
                // Χρησιμοποιούμε την ώρα του ραντεβού για τη νέα επίσκεψη
                dateTime = appt.dateTime,
                notes = appt.notes,
                charge = appt.charge,
                treatment = appt.treatment
            )
            val visitId = db.visitDao().insert(visit)
            db.appointmentDao().updateStatus(appt.id, "COMPLETED")
            visitId
        }
    }

    /**
     * Εναλλακτική: δέχεται appointmentId, το φορτώνει και το μετατρέπει.
     */
    suspend fun convertAppointmentIdToVisit(
        db: PodologiaDatabase,
        appointmentId: Int
    ): Long {
        val appt = db.appointmentDao().getById(appointmentId)
            ?: error("Appointment $appointmentId not found")
        return convertAppointmentToVisit(db, appt)
    }
}
