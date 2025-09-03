package com.kasal.podoapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientHistoryDao {

    // Προσοχή στο tableName: "patient_history"
    @Query("SELECT * FROM patient_history WHERE patientId = :patientId LIMIT 1")
    fun observeByPatientId(patientId: Int): Flow<PatientHistory?>

    @Query("SELECT * FROM patient_history WHERE patientId = :patientId LIMIT 1")
    suspend fun getByPatientId(patientId: Int): PatientHistory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(history: PatientHistory): Long
}
