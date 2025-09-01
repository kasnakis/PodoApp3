package com.kasal.podoapp.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "patient_history",
    indices = [Index(value = ["patientId"], unique = true)]
)
data class PatientHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: Int,

    // Ιατρικά
    val doctorName: String? = null,
    val doctorPhone: String? = null,
    val doctorDiagnosis: String? = null,
    val medication: String? = null,
    val allergies: String? = null,
    val isDiabetic: Boolean = false,
    val diabeticType: String? = null,
    val insulinNotes: String? = null,
    val pillsNotes: String? = null,
    val otherConditions: String? = null,
    val anticoagulantsNotes: String? = null,
    val contagiousDiseasesNotes: String? = null,

    // Στάση/Παραμορφώσεις
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

    // Οίδημα & Κιρσοί
    val edemaLeft: Boolean = false,
    val edemaRight: Boolean = false,
    val varicoseDorsalLeft: Boolean = false,
    val varicoseDorsalRight: Boolean = false,
    val varicosePlantarLeft: Boolean = false,
    val varicosePlantarRight: Boolean = false,

    // Ορθωτικά/Νάρθηκας
    val splintNotes: String? = null,
    val orthoticType: String? = null // "NONE" / "STOCK" / "CUSTOM"
)
