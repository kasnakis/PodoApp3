package com.kasal.podoapp.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Αναμνηστικό (PatientHistory) – one-to-one με Patient.
 * Σημείωση: Κρατάμε τα υπάρχοντα πεδία για συμβατότητα και
 * προσθέτουμε νέα ώστε να καλύψουμε το agreed spec.
 */
@Entity(
    tableName = "patient_history",
    indices = [Index(value = ["patientId"], unique = true)]
)
data class PatientHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: Int,

    // --- Ιατρικά / Στοιχεία Ιατρών & Αγωγής ---
    val doctorName: String? = null,
    val doctorPhone: String? = null,
    val doctorDiagnosis: String? = null,
    val medication: String? = null,
    val allergies: String? = null,

    // Διαβήτης
    val isDiabetic: Boolean = false,
    /** "TYPE_1" | "TYPE_2" (ή null) */
    val diabeticType: String? = null,
    /** ΝΕΟ: Από πότε / σχόλια (free text) */
    val diabetesSinceNotes: String? = null,
    val insulinNotes: String? = null,
    val pillsNotes: String? = null,

    // Άλλες παθήσεις / Αντιπηκτικά / Μεταδοτικά
    /** ΠΑΛΙΟ: ελεύθερο κείμενο. Θα αντικατασταθεί σταδιακά από hasOtherConditions + otherConditionsNotes */
    val otherConditions: String? = null,
    /** ΝΕΟ: flag ύπαρξης λοιπών παθήσεων */
    val hasOtherConditions: Boolean = false,
    /** ΝΕΟ: σημειώσεις για λοιπές παθήσεις */
    val otherConditionsNotes: String? = null,
    val anticoagulantsNotes: String? = null,
    val contagiousDiseasesNotes: String? = null,

    // --- Στάση / Παραμορφώσεις ---
    /** ΠΑΛΙΟ: συνολικό flag. Θα παραμείνει για συμβατότητα */
    val metatarsalDrop: Boolean = false,
    /** ΝΕΟ: αριστερό/δεξί */
    val metatarsalDropLeft: Boolean = false,
    val metatarsalDropRight: Boolean = false,

    val valgus: Boolean = false,
    val varus: Boolean = false,
    val equinus: Boolean = false,
    val cavus: Boolean = false,
    val flatfoot: Boolean = false,
    // Προαιρετικά – υπάρχουν ήδη
    val pronation: Boolean = false,
    val supination: Boolean = false,

    // --- Αριστερό πόδι ---
    val leftHyperkeratosis: Boolean = false,
    val leftHalluxValgus: Boolean = false,
    val leftWarts: Boolean = false,
    val leftDermatophytosis: Boolean = false,

    /** ΝΕΟ: boolean παρουσίας κάλων (ράχης/πέλματος) */
    val cornDorsalLeft: Boolean = false,
    val cornPlantarLeft: Boolean = false,

    // υπάρχοντα notes
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

    /** ΝΕΟ: boolean παρουσίας κάλων (ράχης/πέλματος) */
    val cornDorsalRight: Boolean = false,
    val cornPlantarRight: Boolean = false,

    // υπάρχοντα notes
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
    /** ΝΕΟ: ύπαρξη νάρθηκα; */
    val hasSplint: Boolean = false,
    /** ΝΕΟ: τύπος νάρθηκα / σχόλια */
    val splintType: String? = null,
    /** ΠΑΛΙΟ: κρατιέται ως επιπλέον πεδίο σχολίων */
    val splintNotes: String? = null,

    /** Τύπος ορθωτικών: "NONE" | "STOCK" | "CUSTOM" */
    val orthoticType: String? = null,
    /** ΝΕΟ: αριθμός/κωδικός πάτων */
    val orthoticNumber: String? = null,
    /** ΝΕΟ: σημειώσεις ορθωτικών */
    val orthoticNotes: String? = null
)
