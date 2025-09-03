package com.kasal.podoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientPhotoDao {

    @Query("SELECT * FROM PatientPhoto WHERE patientId = :patientId ORDER BY takenAtMillis DESC")
    fun observePhotosForPatient(patientId: Int): Flow<List<PatientPhoto>>

    @Query("SELECT * FROM PatientPhoto WHERE patientId = :patientId ORDER BY takenAtMillis DESC")
    suspend fun getPhotosForPatient(patientId: Int): List<PatientPhoto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: PatientPhoto): Long

    @Query("DELETE FROM PatientPhoto WHERE id = :photoId")
    suspend fun delete(photoId: Int)
}
