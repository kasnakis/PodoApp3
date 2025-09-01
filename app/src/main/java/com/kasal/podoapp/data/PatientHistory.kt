package com.kasal.podoapp.data

import androidx.room.*

@Entity(
    tableName = "patient_history",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["patientId"], unique = true)]
)
data class PatientHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: Int,

    // Ιατρός & γενικά
    val doctorName: String? = null,
    val doctorPhone: String? = null,
    val doctorDiagnosis: String? = null,
    val medication: String? = null,
    val allergies: String? = null,

    // Διαβήτης
    val isDiabetic: Boolean = false,
    val diabeticType: String? = null, // "TYPE_1" | "TYPE_2"
    val insulinNotes: String? = null,
    val pillsNotes: String? = null,

    // Άλλες παθήσεις
    val otherConditions: String? = null,
    val anticoagulantsNotes: String? = null,
    val contagiousDiseasesNotes: String? = null,

    // Παραμορφώσεις/στάση
    val metatarsalDrop: Boolean = false,
    val valgus: Boolean = false,
    val varus: Boolean = false,
    val equinus: Boolean = false,
    val cavus: Boolean = false,
    val flatfoot: Boolean = false,
    val pronation: Boolean = false,
    val supination: Boolean = false,

    // Αριστερό πόδι
    val leftHyperkeratosis: Boolean = false,
    val leftHalluxValgus: Boolean = false,
    val leftWarts: Boolean = false,
    val leftDermatophytosis: Boolean = false,
    val leftDorsalCallusesNotes: String? = null,
    val leftInterdigitalCallusesNotes: String? = null,
    val leftPlantarCallusesNotes: String? = null,
    val leftHammerToeNotes: String? = null,
    val leftOnychomycosisNotes: String? = null,
    val leftOnychocryptosisNotes: String? = null,
    val leftNailStatusNotes: String? = null,

    // Δεξί πόδι
    val rightHyperkeratosis: Boolean = false,
    val rightHalluxValgus: Boolean = false,
    val rightWarts: Boolean = false,
    val rightDermatophytosis: Boolean = false,
    val rightDorsalCallusesNotes: String? = null,
    val rightInterdigitalCallusesNotes: String? = null,
    val rightPlantarCallusesNotes: String? = null,
    val rightHammerToeNotes: String? = null,
    val rightOnychomycosisNotes: String? = null,
    val rightOnychocryptosisNotes: String? = null,
    val rightNailStatusNotes: String? = null,

    // Οίδημα & κιρσοί
    val edemaLeft: Boolean = false,
    val edemaRight: Boolean = false,
    val varicoseDorsalLeft: Boolean = false,
    val varicoseDorsalRight: Boolean = false,
    val varicosePlantarLeft: Boolean = false,
    val varicosePlantarRight: Boolean = false,

    // Νάρθηκας & πάτοι
    val splintNotes: String? = null,
    val orthoticType: String? = null // "NONE" | "STOCK" | "CUSTOM"
)
