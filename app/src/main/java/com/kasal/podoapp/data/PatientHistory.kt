package com.kasal.podoapp.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Ένα (unique) ιστορικό ανά patientId.
 * Το unique index στο patientId μας επιτρέπει REPLACE στο insert.
 */
@Entity(
    tableName = "patient_history",
    indices = [Index(value = ["patientId"], unique = true)]
)
data class PatientHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: Int,

    // --- Στοιχεία Ιατρών & Αγωγής ---
    val doctorName: String? = null,
    val doctorPhone: String? = null,
    val doctorDiagnosis: String? = null,
    val medication: String? = null,
    val allergies: String? = null,

    // Διαβήτης
    val isDiabetic: Boolean = false,
    /** "TYPE_1" | "TYPE_2" */
    val diabeticType: String? = null,
    val diabetesSinceNotes: String? = null,
    val insulinNotes: String? = null,
    val pillsNotes: String? = null,

    // Άλλες παθήσεις / legacy
    val otherConditions: String? = null,       // (κρατιέται αν υπήρχε)
    val hasOtherConditions: Boolean = false,
    val otherConditionsNotes: String? = null,
    val anticoagulantsNotes: String? = null,
    val contagiousDiseasesNotes: String? = null,

    // --- Πτώση μεταταρσίων / Παραμορφώσεις ---
    val metatarsalDrop: Boolean = false,       // συνολικό (legacy)
    val metatarsalDropLeft: Boolean = false,
    val metatarsalDropRight: Boolean = false,

    val valgus: Boolean = false,
    val varus: Boolean = false,
    val equinus: Boolean = false,
    val cavus: Boolean = false,
    val flatfoot: Boolean = false,
    val pronation: Boolean = false,
    val supination: Boolean = false,

    // --- Αριστερό πόδι ---
    val leftHyperkeratosis: Boolean = false,
    val leftHalluxValgus: Boolean = false,
    val leftWarts: Boolean = false,
    val leftDermatophytosis: Boolean = false,

    // Κάλοι (booleans)
    val cornDorsalLeft: Boolean = false,
    val cornPlantarLeft: Boolean = false,

    // Notes (αριστερό)
    val leftDorsalCallusesNotes: String? = null,
    val leftInterdigitalCallusesNotes: String? = null,
    val leftPlantarCallusesNotes: String? = null,
    val leftHammerToeNotes: String? = null,
    val leftOnychomycosisNotes: String? = null,
    val leftOnychocryptosisNotes: String? = null,
    val leftNailStatusNotes: String? = null,

    // --- Δεξί πόδι ---
    val rightHyperkeratosis: Boolean = false,
    val rightHalluxValgus: Boolean = false,
    val rightWarts: Boolean = false,
    val rightDermatophytosis: Boolean = false,

    // Κάλοι (booleans)
    val cornDorsalRight: Boolean = false,
    val cornPlantarRight: Boolean = false,

    // Notes (δεξί)
    val rightDorsalCallusesNotes: String? = null,
    val rightInterdigitalCallusesNotes: String? = null,
    val rightPlantarCallusesNotes: String? = null,
    val rightHammerToeNotes: String? = null,
    val rightOnychomycosisNotes: String? = null,
    val rightOnychocryptosisNotes: String? = null,
    val rightNailStatusNotes: String? = null,

    // --- Οίδημα & Κιρσοί ---
    val edemaLeft: Boolean = false,
    val edemaRight: Boolean = false,
    val varicoseDorsalLeft: Boolean = false,
    val varicoseDorsalRight: Boolean = false,
    val varicosePlantarLeft: Boolean = false,
    val varicosePlantarRight: Boolean = false,

    // --- Ορθωτικά / Νάρθηκας ---
    val hasSplint: Boolean = false,
    val splintType: String? = null,
    val splintNotes: String? = null,

    /** "NONE" | "STOCK" | "CUSTOM" */
    val orthoticType: String? = null,
    val orthoticNumber: String? = null,
    val orthoticNotes: String? = null
)
