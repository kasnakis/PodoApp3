package com.kasal.podoapp.data

import androidx.room.TypeConverter

class PhotoUriConverter {

    @TypeConverter
    fun fromUriList(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toUriList(data: String): List<String> {
        return if (data.isEmpty()) emptyList() else data.split(",")
    }
}