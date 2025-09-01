package com.kasal.podoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitDao {
    @Insert
    suspend fun insert(visit: Visit): Long

    @Query("SELECT * FROM visits WHERE patientId = :patientId ORDER BY dateTime DESC")
    fun forPatient(patientId: Int): Flow<List<Visit>>

    @Query("SELECT * FROM visits WHERE dateTime BETWEEN :start AND :end ORDER BY dateTime ASC")
    fun getVisitsForDate(start: Long, end: Long): Flow<List<Visit>>
}
