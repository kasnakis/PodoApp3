package com.kasal.podoapp.ui

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.R
import com.kasal.podoapp.PatientPhotoAdapter
import com.kasal.podoapp.data.Patient
import com.kasal.podoapp.data.PatientHistory
import com.kasal.podoapp.data.PatientPhoto
import com.kasal.podoapp.data.PodologiaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


class PatientDetailActivity : AppCompatActivity() {

    private var patientId: Int = 0
    private var existing: PatientHistory? = null

    // Header name
    private lateinit var textPatientNameHeader: TextView

    // Views (History form)
    private lateinit var etDoctorName: EditText
    private lateinit var etDoctorPhone: EditText
    private lateinit var etDiagnosis: EditText
    private lateinit var etMedication: EditText
    private lateinit var etAllergies: EditText

    private lateinit var switchDiabetic: Switch
    private lateinit var spinnerDiabeticType: Spinner
    private lateinit var etInsulinNotes: EditText
    private lateinit var etPillsNotes: EditText

    private lateinit var cbMetatarsalDrop: CheckBox
    private lateinit var cbValgus: CheckBox
    private lateinit var cbVarus: CheckBox
    private lateinit var cbEquinus: CheckBox
    private lateinit var cbCavus: CheckBox
    private lateinit var cbFlatfoot: CheckBox
    private lateinit var cbPronation: CheckBox
    private lateinit var cbSupination: CheckBox

    // Vascular (Right)
    private lateinit var cbEdemaRight: CheckBox
    private lateinit var cbVaricoseDorsalRight: CheckBox
    private lateinit var cbVaricosePlantarRight: CheckBox

    private lateinit var etSplintNotes: EditText
    private lateinit var spinnerOrthoticType: Spinner

    private lateinit var btnSave: Button

    private lateinit var btnVisitHistory: Button
    private lateinit var btnPatientAppointments: Button
    private lateinit var btnNewAppointment: Button

    // Photos UI
    private lateinit var btnAddFromCamera: Button
    private lateinit var btnAddFromGallery: Button
    private lateinit var recyclerPatientPhotos: RecyclerView
    private lateinit var photoAdapter: PatientPhotoAdapter
    private var pendingCameraUri: Uri? = null

    private val orthoticTypes = arrayOf("NONE", "STOCK", "CUSTOM")
    private val diabeticTypes = arrayOf("", "TYPE_1", "TYPE_2")

    // Permissions (images)
    private val requestPhotosPerms = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants.values.all { it }
        if (!granted) {
            Toast.makeText(this, "Δεν δόθηκε άδεια για πρόσβαση σε εικόνες", Toast.LENGTH_SHORT).show()
        } else {
            openGalleryPicker()
        }
    }

    // Gallery picker
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            importAndSaveFromUri(uri) // ✅ αντί για savePhotoUri
        }
    }

    // Camera capture (with EXTRA_OUTPUT)
    private val takePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingCameraUri
        if (success && uri != null) {
            savePhotoUri(uri)
        } else {
            // καθάρισμα του entry σε περίπτωση ακύρωσης
            if (uri != null) {
                contentResolver.delete(uri, null, null)
            }
        }
        pendingCameraUri = null
    }

    // Νέοι launchers για τις άδειες της κάμερας
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startCameraNow() // προχώρα στη λήψη
            } else {
                Toast.makeText(this, "Η κάμερα δεν έχει άδεια.", Toast.LENGTH_LONG).show()
            }
        }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val allGranted = result.values.all { it }
            if (allGranted) {
                startCameraNow()
            } else {
                Toast.makeText(this, "Χρειάζονται άδειες κάμερας/αποθήκευσης.", Toast.LENGTH_LONG).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_detail)

        // Διαβάζουμε patientId με ασφαλές fallback από serialized Patient
        val extraId = intent.getIntExtra("patientId", 0)
        patientId = if (extraId > 0) {
            extraId
        } else {
            val p = intent.getSerializableExtra("patient") as? Patient
            p?.id ?: 0
        }
        if (patientId <= 0) {
            Toast.makeText(this, "Άκυρο patientId", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Bind όλων των views (συμπεριλαμβανομένης της ενότητας φωτογραφιών)
        bindViews()

        // Τίτλος & header
        setPatientNameInTitleAndHeader(patientId)

        // Κουμπιά πλοήγησης
        btnVisitHistory.setOnClickListener {
            val i = Intent(this, VisitListActivity::class.java)
            i.putExtra("patientId", patientId)
            startActivity(i)
        }
        btnPatientAppointments.setOnClickListener {
            val i = Intent(this, AppointmentActivity::class.java)
            i.putExtra("patientId", patientId)
            startActivity(i)
        }
        btnNewAppointment.setOnClickListener {
            val i = Intent(this, NewAppointmentActivity::class.java)
            i.putExtra("patientId", patientId)
            startActivity(i)
        }

        setupSpinners()

        val db = PodologiaDatabase.getDatabase(this)

        // Φόρτωση υπάρχοντος ιστορικού (αν υπάρχει)
        lifecycleScope.launch {
            existing = withContext(Dispatchers.IO) {
                db.patientHistoryDao().getByPatientId(patientId)
            }
            existing?.let { populate(it) }

            btnSave.setOnClickListener {
                val history = collectForm(existing?.id)
                lifecycleScope.launch(Dispatchers.IO) {
                    db.patientHistoryDao().upsert(history)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@PatientDetailActivity,
                            "Αποθηκεύτηκε το Αναμνηστικό",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        }

        // Photos section
        photoAdapter = PatientPhotoAdapter(
            onClick = { photo ->
                val i = Intent(this, FullscreenPhotoActivity::class.java)
                i.putExtra("photoUri", photo.photoUri)
                i.putExtra("takenAtMillis", photo.takenAtMillis)
                startActivity(i)
            },
            onLongClick = { photo -> confirmDeletePhoto(photo) }
        )
        recyclerPatientPhotos.layoutManager = GridLayoutManager(this, 3)
        recyclerPatientPhotos.adapter = photoAdapter

        btnAddFromGallery.setOnClickListener { ensurePhotosPermissionThenOpenGallery() }
        btnAddFromCamera.setOnClickListener { captureFromCamera() }

        observePhotos()
        migrateOldPickerUrisIfAny()
    }

    // Helper: εντοπίζει CheckBox δοκιμάζοντας πιθανά ids (τρέχον + παλιά/typos)
    private fun findCheckBox(vararg names: String): CheckBox {
        for (name in names) {
            val id = resources.getIdentifier(name, "id", packageName)
            if (id != 0) {
                val v: CheckBox? = findViewById(id)
                if (v != null) return v
            }
        }
        error("Δεν βρέθηκε κανένα από τα ids: ${names.joinToString()}")
    }

    private fun setPatientNameInTitleAndHeader(id: Int) {
        val db = PodologiaDatabase.getDatabase(this)
        // Φέρε από Room σε background thread
        CoroutineScope(Dispatchers.IO).launch {
            val patient = db.patientDao().getPatientById(id)
            withContext(Dispatchers.Main) {
                if (patient != null) {
                    val full = patient.fullName
                    supportActionBar?.title = full
                    supportActionBar?.subtitle = "ID #${patient.id}"
                    textPatientNameHeader.text = full
                } else {
                    val fallback = "Πελάτης #$id"
                    supportActionBar?.title = fallback
                    textPatientNameHeader.text = fallback
                }
            }
        }
    }

    private fun bindViews() {
        textPatientNameHeader = findViewById(R.id.textPatientNameHeader)

        etDoctorName = findViewById(R.id.etDoctorName)
        etDoctorPhone = findViewById(R.id.etDoctorPhone)
        etDiagnosis = findViewById(R.id.etDiagnosis)
        etMedication = findViewById(R.id.etMedication)
        etAllergies = findViewById(R.id.etAllergies)

        switchDiabetic = findViewById(R.id.switchDiabetic)
        spinnerDiabeticType = findViewById(R.id.spinnerDiabeticType)
        etInsulinNotes = findViewById(R.id.etInsulinNotes)
        etPillsNotes = findViewById(R.id.etPillsNotes)

        cbMetatarsalDrop = findViewById(R.id.cbMetatarsalDrop)
        cbValgus = findViewById(R.id.cbValgus)
        cbVarus = findViewById(R.id.cbVarus)
        cbEquinus = findViewById(R.id.cbEquinus)
        cbCavus = findViewById(R.id.cbCavus)
        cbFlatfoot = findViewById(R.id.cbFlatfoot)
        cbPronation = findViewById(R.id.cbPronation)
        cbSupination = findViewById(R.id.cbSupination)

        // Δοκιμάζουμε πρώτα τα σωστά ids, μετά παλιότερα/typos για συμβατότητα
        cbEdemaRight = findCheckBox("cbEdemaRight", "cbedemaRight", "cbEdema_right")
        cbVaricoseDorsalRight = findCheckBox("cbVaricoseDorsalRight", "cbvaricoseDorsalRight")
        cbVaricosePlantarRight = findCheckBox("cbVaricosePlantarRight", "cbvaricosePlantarRight")

        etSplintNotes = findViewById(R.id.etSplintNotes)
        spinnerOrthoticType = findViewById(R.id.spinnerOrthoticType)

        btnSave = findViewById(R.id.btnSaveHistory)

        btnVisitHistory = findViewById(R.id.btnVisitHistory)
        btnPatientAppointments = findViewById(R.id.btnPatientAppointments)
        btnNewAppointment = findViewById(R.id.btnNewAppointment)

        // Photos section
        btnAddFromCamera = findViewById(R.id.btnAddFromCamera)
        btnAddFromGallery = findViewById(R.id.btnAddFromGallery)
        recyclerPatientPhotos = findViewById(R.id.recyclerPatientPhotos)
    }

    private fun setupSpinners() {
        spinnerDiabeticType.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, diabeticTypes)
        spinnerOrthoticType.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, orthoticTypes)
    }

    private fun populate(h: PatientHistory) {
        etDoctorName.setText(h.doctorName ?: "")
        etDoctorPhone.setText(h.doctorPhone ?: "")
        etDiagnosis.setText(h.doctorDiagnosis ?: "")
        etMedication.setText(h.medication ?: "")
        etAllergies.setText(h.allergies ?: "")

        switchDiabetic.isChecked = h.isDiabetic

        val diabIndex = diabeticTypes.indexOf(h.diabeticType ?: "")
        spinnerDiabeticType.setSelection(if (diabIndex >= 0) diabIndex else 0)

        etInsulinNotes.setText(h.insulinNotes ?: "")
        etPillsNotes.setText(h.pillsNotes ?: "")

        cbMetatarsalDrop.isChecked = h.metatarsalDrop
        cbValgus.isChecked = h.valgus
        cbVarus.isChecked = h.varus
        cbEquinus.isChecked = h.equinus
        cbCavus.isChecked = h.cavus
        cbFlatfoot.isChecked = h.flatfoot
        cbPronation.isChecked = h.pronation
        cbSupination.isChecked = h.supination

        // Vascular (Right)
        cbEdemaRight.isChecked = h.edemaRight
        cbVaricoseDorsalRight.isChecked = h.varicoseDorsalRight
        cbVaricosePlantarRight.isChecked = h.varicosePlantarRight

        etSplintNotes.setText(h.splintNotes ?: "")

        val orthoIndex = orthoticTypes.indexOf(h.orthoticType ?: "NONE")
        spinnerOrthoticType.setSelection(if (orthoIndex >= 0) orthoIndex else 0)
    }

    private fun collectForm(existingId: Int?): PatientHistory {
        return PatientHistory(
            id = existingId ?: 0,
            patientId = patientId,

            doctorName = etDoctorName.text.toString().trim().ifEmpty { null },
            doctorPhone = etDoctorPhone.text.toString().trim().ifEmpty { null },
            doctorDiagnosis = etDiagnosis.text.toString().trim().ifEmpty { null },
            medication = etMedication.text.toString().trim().ifEmpty { null },
            allergies = etAllergies.text.toString().trim().ifEmpty { null },

            isDiabetic = switchDiabetic.isChecked,
            diabeticType = diabeticTypes[spinnerDiabeticType.selectedItemPosition].ifEmpty { null },
            insulinNotes = etInsulinNotes.text.toString().trim().ifEmpty { null },
            pillsNotes = etPillsNotes.text.toString().trim().ifEmpty { null },

            metatarsalDrop = cbMetatarsalDrop.isChecked,
            valgus = cbValgus.isChecked,
            varus = cbVarus.isChecked,
            equinus = cbEquinus.isChecked,
            cavus = cbCavus.isChecked,
            flatfoot = cbFlatfoot.isChecked,
            pronation = cbPronation.isChecked,
            supination = cbSupination.isChecked,

            // Προς το παρόν μόνο τα Right vascular από αυτό το Activity
            edemaLeft = existing?.edemaLeft ?: false,
            edemaRight = cbEdemaRight.isChecked,
            varicoseDorsalLeft = existing?.varicoseDorsalLeft ?: false,
            varicoseDorsalRight = cbVaricoseDorsalRight.isChecked,
            varicosePlantarLeft = existing?.varicosePlantarLeft ?: false,
            varicosePlantarRight = cbVaricosePlantarRight.isChecked,

            splintNotes = etSplintNotes.text.toString().trim().ifEmpty { null },
            orthoticType = orthoticTypes[spinnerOrthoticType.selectedItemPosition]
        )
    }

    /** ---------------- Photos: observe / permissions / gallery / camera / save ---------------- **/

    private fun observePhotos() {
        val db = PodologiaDatabase.getDatabase(this)
        lifecycleScope.launch {
            db.patientPhotoDao().observePhotosForPatient(patientId).collect { list ->
                photoAdapter.submitList(list)
            }
        }
    }

    private fun ensurePhotosPermissionThenOpenGallery() {
        val perms = if (Build.VERSION.SDK_INT >= 33) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        requestPhotosPerms.launch(perms)
    }

    private fun openGalleryPicker() {
        pickImage.launch("image/*")
    }

    private fun captureFromCamera() {
        // Android 13+ (API 33+): ζητάμε μόνο CAMERA
        if (Build.VERSION.SDK_INT >= 33) {
            val camGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            if (!camGranted) {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
                return
            }
            startCameraNow()
            return
        }

        // Android 10-12 (API 29..32): συνήθως φτάνει η CAMERA (χρησιμοποιούμε MediaStore), αλλά αν χρειαστεί ανά ROM
        if (Build.VERSION.SDK_INT in 29..32) {
            val needs = mutableListOf<String>()
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                needs += Manifest.permission.CAMERA
            }
            // σε κάποιες ROMs μπορεί να ζητηθεί και read:
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                needs += Manifest.permission.READ_EXTERNAL_STORAGE
            }
            if (needs.isNotEmpty()) {
                requestMultiplePermissions.launch(needs.toTypedArray())
                return
            }
            startCameraNow()
            return
        }

        // Android 7–9 (API 24..28): χρειάζεται συχνά ΚΑΙ WRITE_EXTERNAL_STORAGE για το insert στο MediaStore
        val needs = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            needs += Manifest.permission.CAMERA
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            needs += Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            needs += Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (needs.isNotEmpty()) {
            requestMultiplePermissions.launch(needs.toTypedArray())
            return
        }
        startCameraNow()
    }

    private fun startCameraNow() {
        val now = System.currentTimeMillis()
        val name = "podoapp_${patientId}_$now.jpg"

        val collection = if (Build.VERSION.SDK_INT >= 29) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= 29) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PodoApp")
                put(MediaStore.Images.Media.DATE_TAKEN, now)
                put(MediaStore.Images.Media.DATE_ADDED, now / 1000)
            }
        }

        val uri = contentResolver.insert(collection, values)
        if (uri == null) {
            Toast.makeText(this, "Αποτυχία δημιουργίας αρχείου εικόνας", Toast.LENGTH_SHORT).show()
            return
        }
        pendingCameraUri = uri

        // εδώ χρησιμοποιείς ήδη ActivityResultContracts.TakePicture()
        takePicture.launch(uri)
    }

    private fun savePhotoUri(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = PodologiaDatabase.getDatabase(this@PatientDetailActivity)
            db.patientPhotoDao().insert(
                PatientPhoto(
                    patientId = patientId,
                    photoUri = uri.toString(),
                    takenAtMillis = System.currentTimeMillis()
                )
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(this@PatientDetailActivity, "Προστέθηκε φωτογραφία", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun importAndSaveFromUri(src: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            val dest = copyImageToAppAlbum(src)
            if (dest == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PatientDetailActivity, "Αποτυχία εισαγωγής εικόνας", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            PodologiaDatabase.getDatabase(this@PatientDetailActivity)
                .patientPhotoDao()
                .insert(
                    com.kasal.podoapp.data.PatientPhoto(
                        patientId = patientId,
                        photoUri = dest.toString(),
                        takenAtMillis = System.currentTimeMillis()
                    )
                )
            withContext(Dispatchers.Main) {
                Toast.makeText(this@PatientDetailActivity, "Η φωτογραφία προστέθηκε", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** Αντιγράφει το src URI σε MediaStore (Pictures/PodoApp) και επιστρέφει το νέο μόνιμο URI. */
    private fun copyImageToAppAlbum(src: Uri): Uri? {
        return try {
            val now = System.currentTimeMillis()
            val name = "podoapp_${patientId}_$now.jpg"
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATE_TAKEN, now)
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PodoApp")
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            val dest = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            ) ?: return null

            contentResolver.openInputStream(src)?.use { input ->
                contentResolver.openOutputStream(dest)?.use { output ->
                    input.copyTo(output)
                } ?: return null
            } ?: return null

            dest
        } catch (_: Exception) {
            null
        }
    }

    private fun migrateOldPickerUrisIfAny() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = PodologiaDatabase.getDatabase(this@PatientDetailActivity)
            val list = db.patientPhotoDao().getPhotosForPatient(patientId)
            for (photo in list) {
                val u = Uri.parse(photo.photoUri)
                if (u.authority?.contains("photopicker") == true) {
                    val dest = copyImageToAppAlbum(u)
                    if (dest != null) {
                        db.patientPhotoDao().updateUri(photo.id, dest.toString())
                    }
                }
            }
        }
    }

    private fun confirmDeletePhoto(photo: PatientPhoto) {
        AlertDialog.Builder(this)
            .setTitle("Διαγραφή φωτογραφίας;")
            .setMessage("Η φωτογραφία θα αφαιρεθεί από την καρτέλα. Να συνεχίσω;")
            .setPositiveButton("Διαγραφή") { _, _ -> deletePhoto(photo) }
            .setNegativeButton("Άκυρο", null)
            .show()
    }

    private fun deletePhoto(photo: PatientPhoto) {
        lifecycleScope.launch(Dispatchers.IO) {
            // 1) Διαγραφή από Room
            PodologiaDatabase.getDatabase(this@PatientDetailActivity)
                .patientPhotoDao()
                .delete(photo.id)
            // 2) Προσπάθεια διαγραφής και από MediaStore (προαιρετικό)
            try { contentResolver.delete(Uri.parse(photo.photoUri), null, null) } catch (_: Exception) {}
            withContext(Dispatchers.Main) {
                Toast.makeText(this@PatientDetailActivity, "Διαγράφηκε", Toast.LENGTH_SHORT).show()
            }
        }
    }
}