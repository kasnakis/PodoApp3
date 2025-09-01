package com.kasal.podoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Patient::class, PatientHistory::class, Appointment::class, Visit::class],
    version = 2
)
abstract class PodologiaDatabase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun patientHistoryDao(): PatientHistoryDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun visitDao(): VisitDao

    companion object {
        @Volatile
        private var INSTANCE: PodologiaDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Προσθήκη νέων πεδίων στον πίνακα patients
                db.execSQL("ALTER TABLE patients ADD COLUMN cardCode TEXT")
                db.execSQL("ALTER TABLE patients ADD COLUMN email TEXT")
                db.execSQL("ALTER TABLE patients ADD COLUMN birthDate TEXT")
                db.execSQL("ALTER TABLE patients ADD COLUMN profession TEXT")
                // + Συμβατότητα με παλαιά Activities/Adapters
                db.execSQL("ALTER TABLE patients ADD COLUMN address TEXT")
                db.execSQL("ALTER TABLE patients ADD COLUMN category TEXT")

                // Δημιουργία patient_history
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS patient_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        patientId INTEGER NOT NULL,
                        doctorName TEXT,
                        doctorPhone TEXT,
                        doctorDiagnosis TEXT,
                        medication TEXT,
                        allergies TEXT,
                        isDiabetic INTEGER NOT NULL DEFAULT 0,
                        diabeticType TEXT,
                        insulinNotes TEXT,
                        pillsNotes TEXT,
                        otherConditions TEXT,
                        anticoagulantsNotes TEXT,
                        contagiousDiseasesNotes TEXT,
                        metatarsalDrop INTEGER NOT NULL DEFAULT 0,
                        valgus INTEGER NOT NULL DEFAULT 0,
                        varus INTEGER NOT NULL DEFAULT 0,
                        equinus INTEGER NOT NULL DEFAULT 0,
                        cavus INTEGER NOT NULL DEFAULT 0,
                        flatfoot INTEGER NOT NULL DEFAULT 0,
                        pronation INTEGER NOT NULL DEFAULT 0,
                        supination INTEGER NOT NULL DEFAULT 0,
                        leftHyperkeratosis INTEGER NOT NULL DEFAULT 0,
                        leftHalluxValgus INTEGER NOT NULL DEFAULT 0,
                        leftWarts INTEGER NOT NULL DEFAULT 0,
                        leftDermatophytosis INTEGER NOT NULL DEFAULT 0,
                        leftDorsalCallusesNotes TEXT,
                        leftInterdigitalCallusesNotes TEXT,
                        leftPlantarCallusesNotes TEXT,
                        leftHammerToeNotes TEXT,
                        leftOnychomycosisNotes TEXT,
                        leftOnychocryptosisNotes TEXT,
                        leftNailStatusNotes TEXT,
                        rightHyperkeratosis INTEGER NOT NULL DEFAULT 0,
                        rightHalluxValgus INTEGER NOT NULL DEFAULT 0,
                        rightWarts INTEGER NOT NULL DEFAULT 0,
                        rightDermatophytosis INTEGER NOT NULL DEFAULT 0,
                        rightDorsalCallusesNotes TEXT,
                        rightInterdigitalCallusesNotes TEXT,
                        rightPlantarCallusesNotes TEXT,
                        rightHammerToeNotes TEXT,
                        rightOnychomycosisNotes TEXT,
                        rightOnychocryptosisNotes TEXT,
                        rightNailStatusNotes TEXT,
                        edemaLeft INTEGER NOT NULL DEFAULT 0,
                        edemaRight INTEGER NOT NULL DEFAULT 0,
                        varicoseDorsalLeft INTEGER NOT NULL DEFAULT 0,
                        varicoseDorsalRight INTEGER NOT NULL DEFAULT 0,
                        varicosePlantarLeft INTEGER NOT NULL DEFAULT 0,
                        varicosePlantarRight INTEGER NOT NULL DEFAULT 0,
                        splintNotes TEXT,
                        orthoticType TEXT,
                        FOREIGN KEY(patientId) REFERENCES patients(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_patient_history_patientId ON patient_history(patientId)")

                // Δημιουργία appointments
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS appointments (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        patientId INTEGER NOT NULL,
                        dateTime INTEGER NOT NULL,
                        notes TEXT,
                        charge TEXT,
                        treatment TEXT,
                        status TEXT NOT NULL,
                        FOREIGN KEY(patientId) REFERENCES patients(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_appointments_patientId ON appointments(patientId)")

                // Δημιουργία visits
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS visits (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        patientId INTEGER NOT NULL,
                        appointmentId INTEGER,
                        dateTime INTEGER NOT NULL,
                        notes TEXT,
                        charge TEXT,
                        treatment TEXT,
                        FOREIGN KEY(patientId) REFERENCES patients(id) ON DELETE CASCADE,
                        FOREIGN KEY(appointmentId) REFERENCES appointments(id) ON DELETE SET NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_visits_patientId ON visits(patientId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_visits_appointmentId ON visits(appointmentId)")
            }
        }

        fun getDatabase(context: Context): PodologiaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PodologiaDatabase::class.java,
                    "podologia_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
