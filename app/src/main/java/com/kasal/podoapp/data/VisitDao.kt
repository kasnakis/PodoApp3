package com.kasal.podoapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitDao {

    @Insert
    suspend fun insert(visit: Visit): Long

    @Update
    suspend fun update(visit: Visit)

    @Delete
    suspend fun delete(visit: Visit)

    @Query("""
        SELECT * FROM visits
        WHERE patientId = :patientId
        ORDER BY dateTime DESC
    """)
    fun forPatient(patientId: Int): Flow<List<Visit>>

    @Query("""
        SELECT * FROM visits
        WHERE dateTime BETWEEN :start AND :end
        ORDER BY dateTime ASC
    """)
    suspend fun getVisitsForDate(start: Long, end: Long): List<Visit>

    @Query("SELECT * FROM visits WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Visit?
}
