package com.kasal.podoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: PatientHistory): Long

    @Update
    suspend fun update(history: PatientHistory)

    @Query("SELECT * FROM patient_history WHERE patientId = :patientId LIMIT 1")
    fun observeByPatientId(patientId: Int): Flow<PatientHistory?>

    @Query("SELECT * FROM patient_history WHERE patientId = :patientId LIMIT 1")
    suspend fun getByPatientId(patientId: Int): PatientHistory?

    @Transaction
    suspend fun upsert(history: PatientHistory) {
        val existing = getByPatientId(history.patientId)
        if (existing == null) {
            insert(history)
        } else {
            update(history.copy(id = existing.id))
        }
    }
}
