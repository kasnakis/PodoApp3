package com.kasal.podoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Patient::class,
        PatientHistory::class,
        Visit::class,
        Appointment::class,
        PatientPhoto::class
    ],
    version = 5,
    exportSchema = true
)
abstract class PodologiaDatabase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun visitDao(): VisitDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun patientPhotoDao(): PatientPhotoDao
    abstract fun patientHistoryDao(): PatientHistoryDao

    companion object {
        @Volatile private var INSTANCE: PodologiaDatabase? = null

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // --- Νέα πεδία ---
                db.execSQL("ALTER TABLE patient_history ADD COLUMN diabetesSinceNotes TEXT")
                db.execSQL("ALTER TABLE patient_history ADD COLUMN hasOtherConditions INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE patient_history ADD COLUMN otherConditionsNotes TEXT")

                db.execSQL("ALTER TABLE patient_history ADD COLUMN metatarsalDropLeft INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE patient_history ADD COLUMN metatarsalDropRight INTEGER NOT NULL DEFAULT 0")

                db.execSQL("ALTER TABLE patient_history ADD COLUMN cornDorsalLeft INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE patient_history ADD COLUMN cornPlantarLeft INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE patient_history ADD COLUMN cornDorsalRight INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE patient_history ADD COLUMN cornPlantarRight INTEGER NOT NULL DEFAULT 0")

                db.execSQL("ALTER TABLE patient_history ADD COLUMN hasSplint INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE patient_history ADD COLUMN splintType TEXT")
                db.execSQL("ALTER TABLE patient_history ADD COLUMN orthoticNumber TEXT")
                db.execSQL("ALTER TABLE patient_history ADD COLUMN orthoticNotes TEXT")

                // --- "Έξυπνο" prefill από υπάρχοντα δεδομένα ---
                // hasOtherConditions από το παλιό free-text otherConditions
                db.execSQL(
                    """
                        UPDATE patient_history
                        SET hasOtherConditions = CASE
                          WHEN otherConditions IS NOT NULL AND TRIM(otherConditions) <> '' THEN 1 ELSE 0
                        END
                    """.trimIndent()
                )

                // Κατανομή του παλιού single metatarsalDrop -> L/R
                db.execSQL("UPDATE patient_history SET metatarsalDropLeft = CASE WHEN metatarsalDrop = 1 THEN 1 ELSE 0 END")
                db.execSQL("UPDATE patient_history SET metatarsalDropRight = CASE WHEN metatarsalDrop = 1 THEN 1 ELSE 0 END")

                // Αν υπάρχουν notes για κάλους, τικάρουμε τα νέα booleans
                db.execSQL("UPDATE patient_history SET cornDorsalLeft  = CASE WHEN leftDorsalCallusesNotes  IS NOT NULL AND TRIM(leftDorsalCallusesNotes)  <> '' THEN 1 ELSE 0 END")
                db.execSQL("UPDATE patient_history SET cornPlantarLeft = CASE WHEN leftPlantarCallusesNotes IS NOT NULL AND TRIM(leftPlantarCallusesNotes) <> '' THEN 1 ELSE 0 END")
                db.execSQL("UPDATE patient_history SET cornDorsalRight = CASE WHEN rightDorsalCallusesNotes IS NOT NULL AND TRIM(rightDorsalCallusesNotes) <> '' THEN 1 ELSE 0 END")
                db.execSQL("UPDATE patient_history SET cornPlantarRight= CASE WHEN rightPlantarCallusesNotes IS NOT NULL AND TRIM(rightPlantarCallusesNotes)<> '' THEN 1 ELSE 0 END")

                // hasSplint αν υπάρχει ήδη κείμενο στο splintNotes
                db.execSQL("UPDATE patient_history SET hasSplint = CASE WHEN splintNotes IS NOT NULL AND TRIM(splintNotes) <> '' THEN 1 ELSE 0 END")
            }
        }

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
                // Κρατάμε και migration και fallback: αν λείπει migration path -> destructive (dev)
                .addMigrations(MIGRATION_4_5)
                .fallbackToDestructiveMigration()
                .build()
    }
}
