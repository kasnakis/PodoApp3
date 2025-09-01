package com.kasal.podoapp.ui

import com.kasal.podoapp.data.PatientHistory

class PatientHistoryAggregator(
    private val patientId: Int,
    private val existing: PatientHistory?
) {
    // Ιατρικά
    var doctorName: String? = null
    var doctorPhone: String? = null
    var doctorDiagnosis: String? = null
    var medication: String? = null
    var allergies: String? = null
    var isDiabetic: Boolean = false
    var diabeticType: String? = null
    var insulinNotes: String? = null
    var pillsNotes: String? = null
    var otherConditions: String? = null
    var anticoagulantsNotes: String? = null
    var contagiousDiseasesNotes: String? = null

    // Στάση/Παραμορφώσεις
    var metatarsalDrop = false
    var valgus = false
    var varus = false
    var equinus = false
    var cavus = false
    var flatfoot = false
    var pronation = false
    var supination = false

    // Αριστερό πόδι
    var leftHyperkeratosis = false
    var leftHalluxValgus = false
    var leftWarts = false
    var leftDermatophytosis = false
    var leftDorsalCallusesNotes: String? = null
    var leftInterdigitalCallusesNotes: String? = null
    var leftPlantarCallusesNotes: String? = null
    var leftHammerToeNotes: String? = null
    var leftOnychomycosisNotes: String? = null
    var leftOnychocryptosisNotes: String? = null
    var leftNailStatusNotes: String? = null

    // Δεξί πόδι
    var rightHyperkeratosis = false
    var rightHalluxValgus = false
    var rightWarts = false
    var rightDermatophytosis = false
    var rightDorsalCallusesNotes: String? = null
    var rightInterdigitalCallusesNotes: String? = null
    var rightPlantarCallusesNotes: String? = null
    var rightHammerToeNotes: String? = null
    var rightOnychomycosisNotes: String? = null
    var rightOnychocryptosisNotes: String? = null
    var rightNailStatusNotes: String? = null

    // Οίδημα & Κιρσοί
    var edemaLeft = false
    var edemaRight = false
    var varicoseDorsalLeft = false
    var varicoseDorsalRight = false
    var varicosePlantarLeft = false
    var varicosePlantarRight = false

    // Ορθωτικά/Νάρθηκας
    var splintNotes: String? = null
    var orthoticType: String? = null

    fun prefillFrom(h: PatientHistory?) {
        if (h == null) return
        doctorName = h.doctorName; doctorPhone = h.doctorPhone; doctorDiagnosis = h.doctorDiagnosis
        medication = h.medication; allergies = h.allergies
        isDiabetic = h.isDiabetic; diabeticType = h.diabeticType
        insulinNotes = h.insulinNotes; pillsNotes = h.pillsNotes
        otherConditions = h.otherConditions
        anticoagulantsNotes = h.anticoagulantsNotes
        contagiousDiseasesNotes = h.contagiousDiseasesNotes

        metatarsalDrop = h.metatarsalDrop; valgus = h.valgus; varus = h.varus
        equinus = h.equinus; cavus = h.cavus; flatfoot = h.flatfoot
        pronation = h.pronation; supination = h.supination

        leftHyperkeratosis = h.leftHyperkeratosis
        leftHalluxValgus = h.leftHalluxValgus
        leftWarts = h.leftWarts
        leftDermatophytosis = h.leftDermatophytosis
        leftDorsalCallusesNotes = h.leftDorsalCallusesNotes
        leftInterdigitalCallusesNotes = h.leftInterdigitalCallusesNotes
        leftPlantarCallusesNotes = h.leftPlantarCallusesNotes
        leftHammerToeNotes = h.leftHammerToeNotes
        leftOnychomycosisNotes = h.leftOnychomycosisNotes
        leftOnychocryptosisNotes = h.leftOnychocryptosisNotes
        leftNailStatusNotes = h.leftNailStatusNotes

        rightHyperkeratosis = h.rightHyperkeratosis
        rightHalluxValgus = h.rightHalluxValgus
        rightWarts = h.rightWarts
        rightDermatophytosis = h.rightDermatophytosis
        rightDorsalCallusesNotes = h.rightDorsalCallusesNotes
        rightInterdigitalCallusesNotes = h.rightInterdigitalCallusesNotes
        rightPlantarCallusesNotes = h.rightPlantarCallusesNotes
        rightHammerToeNotes = h.rightHammerToeNotes
        rightOnychomycosisNotes = h.rightOnychomycosisNotes
        rightOnychocryptosisNotes = h.rightOnychocryptosisNotes
        rightNailStatusNotes = h.rightNailStatusNotes

        edemaLeft = h.edemaLeft; edemaRight = h.edemaRight
        varicoseDorsalLeft = h.varicoseDorsalLeft; varicoseDorsalRight = h.varicoseDorsalRight
        varicosePlantarLeft = h.varicosePlantarLeft; varicosePlantarRight = h.varicosePlantarRight

        splintNotes = h.splintNotes; orthoticType = h.orthoticType
    }

    fun build(): PatientHistory {
        val id = existing?.id ?: 0
        return PatientHistory(
            id = id, patientId = patientId,
            doctorName = doctorName, doctorPhone = doctorPhone, doctorDiagnosis = doctorDiagnosis,
            medication = medication, allergies = allergies,
            isDiabetic = isDiabetic, diabeticType = diabeticType,
            insulinNotes = insulinNotes, pillsNotes = pillsNotes,
            otherConditions = otherConditions,
            anticoagulantsNotes = anticoagulantsNotes,
            contagiousDiseasesNotes = contagiousDiseasesNotes,
            metatarsalDrop = metatarsalDrop, valgus = valgus, varus = varus,
            equinus = equinus, cavus = cavus, flatfoot = flatfoot, pronation = pronation, supination = supination,
            leftHyperkeratosis = leftHyperkeratosis, leftHalluxValgus = leftHalluxValgus, leftWarts = leftWarts, leftDermatophytosis = leftDermatophytosis,
            leftDorsalCallusesNotes = leftDorsalCallusesNotes, leftInterdigitalCallusesNotes = leftInterdigitalCallusesNotes, leftPlantarCallusesNotes = leftPlantarCallusesNotes,
            leftHammerToeNotes = leftHammerToeNotes, leftOnychomycosisNotes = leftOnychomycosisNotes, leftOnychocryptosisNotes = leftOnychocryptosisNotes, leftNailStatusNotes = leftNailStatusNotes,
            rightHyperkeratosis = rightHyperkeratosis, rightHalluxValgus = rightHalluxValgus, rightWarts = rightWarts, rightDermatophytosis = rightDermatophytosis,
            rightDorsalCallusesNotes = rightDorsalCallusesNotes, rightInterdigitalCallusesNotes = rightInterdigitalCallusesNotes, rightPlantarCallusesNotes = rightPlantarCallusesNotes,
            rightHammerToeNotes = rightHammerToeNotes, rightOnychomycosisNotes = rightOnychomycosisNotes, rightOnychocryptosisNotes = rightOnychocryptosisNotes, rightNailStatusNotes = rightNailStatusNotes,
            edemaLeft = edemaLeft, edemaRight = edemaRight,
            varicoseDorsalLeft = varicoseDorsalLeft, varicoseDorsalRight = varicoseDorsalRight,
            varicosePlantarLeft = varicosePlantarLeft, varicosePlantarRight = varicosePlantarRight,
            splintNotes = splintNotes, orthoticType = orthoticType
        )
    }
}
