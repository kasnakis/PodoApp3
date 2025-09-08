package com.kasal.podoapp.ui

import com.kasal.podoapp.data.PatientHistory

/**
 * Συγκεντρώνει όλα τα πεδία του PatientHistory από τα tabs
 * και παράγει ένα PatientHistory για upsert.
 */
class PatientHistoryAggregator(
    private val patientId: Int,
    existing: PatientHistory?
) {
    // --- Ιατρικά / Στοιχεία Ιατρών & Αγωγής ---
    var doctorName: String? = existing?.doctorName
    var doctorPhone: String? = existing?.doctorPhone
    var doctorDiagnosis: String? = existing?.doctorDiagnosis
    var medication: String? = existing?.medication
    var allergies: String? = existing?.allergies

    // Διαβήτης
    var isDiabetic: Boolean = existing?.isDiabetic ?: false
    var diabeticType: String? = existing?.diabeticType // "TYPE_1" | "TYPE_2" | null
    var diabetesSinceNotes: String? = existing?.diabetesSinceNotes
    var insulinNotes: String? = existing?.insulinNotes
    var pillsNotes: String? = existing?.pillsNotes

    // Άλλες παθήσεις / Αντιπηκτικά / Μεταδοτικά
    var otherConditions: String? = existing?.otherConditions // legacy (κρατιέται)
    var hasOtherConditions: Boolean = existing?.hasOtherConditions ?: false
    var otherConditionsNotes: String? = existing?.otherConditionsNotes
    var anticoagulantsNotes: String? = existing?.anticoagulantsNotes
    var contagiousDiseasesNotes: String? = existing?.contagiousDiseasesNotes

    // --- Στάση / Παραμορφώσεις ---
    var metatarsalDrop: Boolean = existing?.metatarsalDrop ?: false // legacy συνολικό
    var metatarsalDropLeft: Boolean = existing?.metatarsalDropLeft ?: false
    var metatarsalDropRight: Boolean = existing?.metatarsalDropRight ?: false

    var valgus: Boolean = existing?.valgus ?: false
    var varus: Boolean = existing?.varus ?: false
    var equinus: Boolean = existing?.equinus ?: false
    var cavus: Boolean = existing?.cavus ?: false
    var flatfoot: Boolean = existing?.flatfoot ?: false
    var pronation: Boolean = existing?.pronation ?: false
    var supination: Boolean = existing?.supination ?: false

    // --- Αριστερό πόδι ---
    var leftHyperkeratosis: Boolean = existing?.leftHyperkeratosis ?: false
    var leftHalluxValgus: Boolean = existing?.leftHalluxValgus ?: false
    var leftWarts: Boolean = existing?.leftWarts ?: false
    var leftDermatophytosis: Boolean = existing?.leftDermatophytosis ?: false

    // Κάλοι – Νέα booleans
    var cornDorsalLeft: Boolean = existing?.cornDorsalLeft ?: false
    var cornPlantarLeft: Boolean = existing?.cornPlantarLeft ?: false

    // notes
    var leftDorsalCallusesNotes: String? = existing?.leftDorsalCallusesNotes
    var leftInterdigitalCallusesNotes: String? = existing?.leftInterdigitalCallusesNotes
    var leftPlantarCallusesNotes: String? = existing?.leftPlantarCallusesNotes
    var leftHammerToeNotes: String? = existing?.leftHammerToeNotes
    var leftOnychomycosisNotes: String? = existing?.leftOnychomycosisNotes
    var leftOnychocryptosisNotes: String? = existing?.leftOnychocryptosisNotes
    var leftNailStatusNotes: String? = existing?.leftNailStatusNotes

    // --- Δεξί πόδι ---
    var rightHyperkeratosis: Boolean = existing?.rightHyperkeratosis ?: false
    var rightHalluxValgus: Boolean = existing?.rightHalluxValgus ?: false
    var rightWarts: Boolean = existing?.rightWarts ?: false
    var rightDermatophytosis: Boolean = existing?.rightDermatophytosis ?: false

    // Κάλοι – Νέα booleans
    var cornDorsalRight: Boolean = existing?.cornDorsalRight ?: false
    var cornPlantarRight: Boolean = existing?.cornPlantarRight ?: false

    // notes
    var rightDorsalCallusesNotes: String? = existing?.rightDorsalCallusesNotes
    var rightInterdigitalCallusesNotes: String? = existing?.rightInterdigitalCallusesNotes
    var rightPlantarCallusesNotes: String? = existing?.rightPlantarCallusesNotes
    var rightHammerToeNotes: String? = existing?.rightHammerToeNotes
    var rightOnychomycosisNotes: String? = existing?.rightOnychomycosisNotes
    var rightOnychocryptosisNotes: String? = existing?.rightOnychocryptosisNotes
    var rightNailStatusNotes: String? = existing?.rightNailStatusNotes

    // --- Οίδημα & Κιρσοί ---
    var edemaLeft: Boolean = existing?.edemaLeft ?: false
    var edemaRight: Boolean = existing?.edemaRight ?: false
    var varicoseDorsalLeft: Boolean = existing?.varicoseDorsalLeft ?: false
    var varicoseDorsalRight: Boolean = existing?.varicoseDorsalRight ?: false
    var varicosePlantarLeft: Boolean = existing?.varicosePlantarLeft ?: false
    var varicosePlantarRight: Boolean = existing?.varicosePlantarRight ?: false

    // --- Ορθωτικά / Νάρθηκας ---
    var hasSplint: Boolean = existing?.hasSplint ?: false
    var splintType: String? = existing?.splintType
    var splintNotes: String? = existing?.splintNotes

    var orthoticType: String? = existing?.orthoticType // "NONE"|"STOCK"|"CUSTOM"|null
    var orthoticNumber: String? = existing?.orthoticNumber
    var orthoticNotes: String? = existing?.orthoticNotes

    fun build(): PatientHistory {
        // Basic sync
        if (!isDiabetic) {
            diabeticType = null
            diabetesSinceNotes = null
            insulinNotes = null
            pillsNotes = null
        }
        if (!hasOtherConditions) otherConditionsNotes = null

        // legacy συνολικό = OR των L/R
        metatarsalDrop = metatarsalDrop || metatarsalDropLeft || metatarsalDropRight

        if (!hasSplint) {
            splintType = null
        }
        if (orthoticType == "NONE") {
            orthoticNumber = null
            // orthoticNotes = null // αν θέλεις καθάρισμα, ξεκλείδωσε
        }

        return PatientHistory(
            id = existing?.id ?: 0,
            patientId = patientId,

            // Ιατρικά
            doctorName = doctorName,
            doctorPhone = doctorPhone,
            doctorDiagnosis = doctorDiagnosis,
            medication = medication,
            allergies = allergies,

            isDiabetic = isDiabetic,
            diabeticType = diabeticType,
            diabetesSinceNotes = diabetesSinceNotes,
            insulinNotes = insulinNotes,
            pillsNotes = pillsNotes,

            otherConditions = otherConditions,
            hasOtherConditions = hasOtherConditions,
            otherConditionsNotes = otherConditionsNotes,
            anticoagulantsNotes = anticoagulantsNotes,
            contagiousDiseasesNotes = contagiousDiseasesNotes,

            // Παραμορφώσεις
            metatarsalDrop = metatarsalDrop,
            metatarsalDropLeft = metatarsalDropLeft,
            metatarsalDropRight = metatarsalDropRight,
            valgus = valgus,
            varus = varus,
            equinus = equinus,
            cavus = cavus,
            flatfoot = flatfoot,
            pronation = pronation,
            supination = supination,

            // Αριστερό
            leftHyperkeratosis = leftHyperkeratosis,
            leftHalluxValgus = leftHalluxValgus,
            leftWarts = leftWarts,
            leftDermatophytosis = leftDermatophytosis,
            cornDorsalLeft = cornDorsalLeft,
            cornPlantarLeft = cornPlantarLeft,
            leftDorsalCallusesNotes = leftDorsalCallusesNotes,
            leftInterdigitalCallusesNotes = leftInterdigitalCallusesNotes,
            leftPlantarCallusesNotes = leftPlantarCallusesNotes,
            leftHammerToeNotes = leftHammerToeNotes,
            leftOnychomycosisNotes = leftOnychomycosisNotes,
            leftOnychocryptosisNotes = leftOnychocryptosisNotes,
            leftNailStatusNotes = leftNailStatusNotes,

            // Δεξί
            rightHyperkeratosis = rightHyperkeratosis,
            rightHalluxValgus = rightHalluxValgus,
            rightWarts = rightWarts,
            rightDermatophytosis = rightDermatophytosis,
            cornDorsalRight = cornDorsalRight,
            cornPlantarRight = cornPlantarRight,
            rightDorsalCallusesNotes = rightDorsalCallusesNotes,
            rightInterdigitalCallusesNotes = rightInterdigitalCallusesNotes,
            rightPlantarCallusesNotes = rightPlantarCallusesNotes,
            rightHammerToeNotes = rightHammerToeNotes,
            rightOnychomycosisNotes = rightOnychomycosisNotes,
            rightOnychocryptosisNotes = rightOnychocryptosisNotes,
            rightNailStatusNotes = rightNailStatusNotes,

            // Οίδημα & Κιρσοί
            edemaLeft = edemaLeft,
            edemaRight = edemaRight,
            varicoseDorsalLeft = varicoseDorsalLeft,
            varicoseDorsalRight = varicoseDorsalRight,
            varicosePlantarLeft = varicosePlantarLeft,
            varicosePlantarRight = varicosePlantarRight,

            // Ορθωτικά / Νάρθηκας
            hasSplint = hasSplint,
            splintType = splintType,
            splintNotes = splintNotes,
            orthoticType = orthoticType,
            orthoticNumber = orthoticNumber,
            orthoticNotes = orthoticNotes
        )
    }

    private val existing = existing
}
