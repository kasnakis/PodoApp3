package com.kasal.podoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Insert
    suspend fun insert(appointment: Appointment): Long

    @Update
    suspend fun update(appointment: Appointment)

    @Delete
    suspend fun delete(appointment: Appointment)

    @Query("UPDATE appointments SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)

    @Query("SELECT * FROM appointments WHERE patientId = :patientId ORDER BY dateTime DESC")
    fun forPatient(patientId: Int): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE dateTime BETWEEN :start AND :end ORDER BY dateTime ASC")
    fun getAppointmentsForDate(start: Long, end: Long): Flow<List<Appointment>>
}
