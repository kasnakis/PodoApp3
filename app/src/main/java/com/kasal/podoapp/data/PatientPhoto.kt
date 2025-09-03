package com.kasal.podoapp.data

import androidx.room.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patientId")]
)
data class PatientPhoto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: Int,
    /** Stringified URI (MediaStore/SAF) */
    val photoUri: String,
    /** UTC millis της προσθήκης/λήψης */
    val takenAtMillis: Long
)
