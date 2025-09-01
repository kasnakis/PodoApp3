package com.kasal.podoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val phone: String,
    val notes: String? = null,

    // από το νέο “ΝΕΟΣ ΠΕΛΑΤΗΣ”
    val cardCode: String? = null,
    val email: String? = null,
    val birthDate: String? = null,
    val profession: String? = null,

    // συμβατότητα με υπάρχον UI/Adapters
    val address: String? = null,
    val category: String? = null
) : Serializable
