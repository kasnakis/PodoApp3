package com.kasal.podoapp.data

import androidx.room.*

@Entity(
    tableName = "appointments",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patientId")]
)
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: Int,
    val dateTime: Long,          // epoch millis
    val notes: String? = null,
    // Απαιτήσεις αρχείου: Χρέωση & Θεραπεία σε κάθε ραντεβού
    val charge: String? = null,
    val treatment: String? = null,
    val status: String = "SCHEDULED" // SCHEDULED | COMPLETED | CANCELLED
)
