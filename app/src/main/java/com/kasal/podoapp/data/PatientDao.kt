package com.kasal.podoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(patient: Patient)

    @Update
    suspend fun update(patient: Patient)

    @Delete
    suspend fun delete(patient: Patient)

    @Query("SELECT * FROM patients ORDER BY fullName ASC")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patients WHERE id = :id LIMIT 1")
    suspend fun getPatientById(id: Int): Patient?

    @Query("SELECT * FROM patients WHERE fullName LIKE '%' || :q || '%' ORDER BY fullName ASC")
    fun searchByName(q: String): Flow<List<Patient>>
}
