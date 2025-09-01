package com.kasal.podoapp.data

// ------ Appointment "παλιά" aliases ------
val Appointment.datetime: Long
    get() = this.dateTime

val Appointment.type: String
    get() = this.status

// ------ Visit "παλιά" aliases ------
val Visit.reason: String?
    get() = this.notes

val Visit.diagnosis: String?
    get() = null // placeholder (δεν υπάρχει στο νέο schema)

val Visit.photoUris: List<String>
    get() = emptyList() // placeholder συμβατότητας
