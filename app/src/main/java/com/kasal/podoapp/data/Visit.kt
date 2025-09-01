package com.kasal.podoapp.data

import androidx.room.*

@Entity(
    tableName = "visits",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Appointment::class,
            parentColumns = ["id"],
            childColumns = ["appointmentId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("patientId"), Index("appointmentId")]
)
data class Visit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: Int,
    val appointmentId: Int? = null,
    val dateTime: Long,          // epoch millis
    val notes: String? = null,
    val charge: String? = null,
    val treatment: String? = null
)
