package com.kasal.podoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Patient::class,
        PatientHistory::class,
        Visit::class,
        Appointment::class,
        PatientPhoto::class
    ],
    version = 4,
    exportSchema = true
)
abstract class PodologiaDatabase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun visitDao(): VisitDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun patientPhotoDao(): PatientPhotoDao
    // ✅ ΠΡΟΣΘΗΚΗ
    abstract fun patientHistoryDao(): PatientHistoryDao

    companion object {
        @Volatile private var INSTANCE: PodologiaDatabase? = null

        fun getDatabase(context: Context): PodologiaDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDb(context).also { INSTANCE = it }
            }

        fun getInstance(context: Context): PodologiaDatabase = getDatabase(context)

        private fun buildDb(context: Context): PodologiaDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                PodologiaDatabase::class.java,
                "podologia.db"
            )
                // Dev mode: σε production βάλε proper migrations αντί για destructive
                .fallbackToDestructiveMigration()
                .build()
    }
}
