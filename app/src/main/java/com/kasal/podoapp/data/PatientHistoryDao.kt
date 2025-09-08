package com.kasal.podoapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientHistoryDao {

    @Query("SELECT * FROM patient_history WHERE patientId = :patientId LIMIT 1")
    suspend fun getByPatientId(patientId: Int): PatientHistory?

    @Query("SELECT * FROM patient_history WHERE patientId = :patientId LIMIT 1")
    fun observeByPatientId(patientId: Int): Flow<PatientHistory?>

    /**
     * Upsert μέσω REPLACE: λόγω unique index στο patientId, θα αντικατασταθεί η εγγραφή.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(history: PatientHistory): Long
}
