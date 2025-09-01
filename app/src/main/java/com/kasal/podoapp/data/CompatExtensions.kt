package com.kasal.podoapp.data

val Appointment.datetime: Long get() = dateTime
val Appointment.type: String get() = status

val Visit.reason: String? get() = notes
val Visit.diagnosis: String? get() = null        // placeholder, αν δεν κρατάς διάγνωση
val Visit.photoUris: List<String> get() = emptyList() // placeholder
