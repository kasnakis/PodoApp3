package com.kasal.podoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(history: PatientHistory)

    @Query("SELECT * FROM patient_history WHERE patientId = :patientId LIMIT 1")
    fun observeByPatientId(patientId: Int): Flow<PatientHistory?>

    @Query("DELETE FROM patient_history WHERE patientId = :patientId")
    suspend fun deleteByPatientId(patientId: Int)
}
