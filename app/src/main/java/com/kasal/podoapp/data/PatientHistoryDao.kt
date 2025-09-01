package com.kasal.podoapp.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientHistoryDao {

    @Query("SELECT * FROM patient_history WHERE patientId = :patientId LIMIT 1")
    suspend fun getByPatientId(patientId: Int): PatientHistory?

    @Query("SELECT * FROM patient_history WHERE patientId = :patientId LIMIT 1")
    fun observeByPatientId(patientId: Int): Flow<PatientHistory?>

    @Upsert
    suspend fun upsert(history: PatientHistory)
}
