package com.kasal.podoapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {

    @Insert
    suspend fun insert(appointment: Appointment): Long

    @Update
    suspend fun update(appointment: Appointment)

    @Delete
    suspend fun delete(appointment: Appointment)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM appointments WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Appointment?

    @Query("UPDATE appointments SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)

    // Χρησιμοποιείται για να εισάγει ή να ενημερώσει ένα ραντεβού
    @Upsert
    suspend fun upsert(appointment: Appointment)

    // Προαιρετικό syntactic sugar για τη ροή "ολοκλήρωση ραντεβού"
    suspend fun markCompleted(id: Int) = updateStatus(id, "COMPLETED")

    // Προαιρετικό syntactic sugar για να ακυρώσει ένα ραντεβού
    suspend fun markCanceled(id: Int) = updateStatus(id, "CANCELED")

    @Query("""
        SELECT * FROM appointments
        WHERE patientId = :patientId
        ORDER BY dateTime DESC
    """)
    fun forPatient(patientId: Int): Flow<List<Appointment>>

    // Χρησιμοποιείται από το AppointmentCalendarActivity:
    // ζητάμε ραντεβού σε εύρος ημέρας [start, end] (UTC millis)
    @Query("""
        SELECT * FROM appointments
        WHERE dateTime BETWEEN :start AND :end
        ORDER BY dateTime ASC
    """)
    suspend fun getAppointmentsForDate(start: Long, end: Long): List<Appointment>
}