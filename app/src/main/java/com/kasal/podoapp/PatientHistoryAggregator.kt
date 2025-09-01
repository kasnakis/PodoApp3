package com.kasal.podoapp.ui

import com.kasal.podoapp.data.PatientHistory

class PatientHistoryAggregator(
    private val patientId: Int,
    existing: PatientHistory?
) {
    // --- Ιατρικά ---
    var doctorName: String? = existing?.doctorName
    var doctorPhone: String? = existing?.doctorPhone
    var doctorDiagnosis: String? = existing?.doctorDiagnosis
    var medication: String? = existing?.medication
    var allergies: String? = existing?.allergies

    var isDiabetic: Boolean = existing?.isDiabetic ?: false
    var diabeticType: String? = existing?.diabeticType
    var insulinNotes: String? = existing?.insulinNotes
    var pillsNotes: String? = existing?.pillsNotes
    var otherConditions: String? = existing?.otherConditions
    var anticoagulantsNotes: String? = existing?.anticoagulantsNotes
    var contagiousDiseasesNotes: String? = existing?.contagiousDiseasesNotes

    // --- Στάση/Παραμορφώσεις ---
    var metatarsalDrop: Boolean = existing?.metatarsalDrop ?: false
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
    var rightDorsalCallusesNotes: String? = existing?.rightDorsalCallusesNotes
    var rightInterdigitalCallusesNotes: String? = existing?.rightInterdigitalCallusesNotes
    var rightPlantarCallusesNotes: String? = existing?.rightPlantarCallusesNotes
    var rightHammerToeNotes: String? = existing?.rightHammerToeNotes
    var rightOnychomycosisNotes: String? = existing?.rightOnychomycosisNotes
    var rightOnychocryptosisNotes: String? = existing?.rightOnychocryptosisNotes
    var rightNailStatusNotes: String? = existing?.rightNailStatusNotes

    // --- Οίδημα/Κιρσοί ---
    var edemaLeft: Boolean = existing?.edemaLeft ?: false
    var edemaRight: Boolean = existing?.edemaRight ?: false
    var varicoseDorsalLeft: Boolean = existing?.varicoseDorsalLeft ?: false
    var varicoseDorsalRight: Boolean = existing?.varicoseDorsalRight ?: false
    var varicosePlantarLeft: Boolean = existing?.varicosePlantarLeft ?: false
    var varicosePlantarRight: Boolean = existing?.varicosePlantarRight ?: false

    // --- Ορθωτικά/Νάρθηκας ---
    var splintNotes: String? = existing?.splintNotes
    var orthoticType: String? = existing?.orthoticType

    fun build(): PatientHistory {
        return PatientHistory(
            id = existing?.id ?: 0,
            patientId = patientId,

            doctorName = doctorName,
            doctorPhone = doctorPhone,
            doctorDiagnosis = doctorDiagnosis,
            medication = medication,
            allergies = allergies,

            isDiabetic = isDiabetic,
            diabeticType = diabeticType,
            insulinNotes = insulinNotes,
            pillsNotes = pillsNotes,
            otherConditions = otherConditions,
            anticoagulantsNotes = anticoagulantsNotes,
            contagiousDiseasesNotes = contagiousDiseasesNotes,

            metatarsalDrop = metatarsalDrop,
            valgus = valgus,
            varus = varus,
            equinus = equinus,
            cavus = cavus,
            flatfoot = flatfoot,
            pronation = pronation,
            supination = supination,

            leftHyperkeratosis = leftHyperkeratosis,
            leftHalluxValgus = leftHalluxValgus,
            leftWarts = leftWarts,
            leftDermatophytosis = leftDermatophytosis,
            leftDorsalCallusesNotes = leftDorsalCallusesNotes,
            leftInterdigitalCallusesNotes = leftInterdigitalCallusesNotes,
            leftPlantarCallusesNotes = leftPlantarCallusesNotes,
            leftHammerToeNotes = leftHammerToeNotes,
            leftOnychomycosisNotes = leftOnychomycosisNotes,
            leftOnychocryptosisNotes = leftOnychocryptosisNotes,
            leftNailStatusNotes = leftNailStatusNotes,

            rightHyperkeratosis = rightHyperkeratosis,
            rightHalluxValgus = rightHalluxValgus,
            rightWarts = rightWarts,
            rightDermatophytosis = rightDermatophytosis,
            rightDorsalCallusesNotes = rightDorsalCallusesNotes,
            rightInterdigitalCallusesNotes = rightInterdigitalCallusesNotes,
            rightPlantarCallusesNotes = rightPlantarCallusesNotes,
            rightHammerToeNotes = rightHammerToeNotes,
            rightOnychomycosisNotes = rightOnychomycosisNotes,
            rightOnychocryptosisNotes = rightOnychocryptosisNotes,
            rightNailStatusNotes = rightNailStatusNotes,

            edemaLeft = edemaLeft,
            edemaRight = edemaRight,
            varicoseDorsalLeft = varicoseDorsalLeft,
            varicoseDorsalRight = varicoseDorsalRight,
            varicosePlantarLeft = varicosePlantarLeft,
            varicosePlantarRight = varicosePlantarRight,

            splintNotes = splintNotes,
            orthoticType = orthoticType
        )
    }

    private val existing = existing
}
