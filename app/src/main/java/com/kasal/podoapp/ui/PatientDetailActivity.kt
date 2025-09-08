package com.kasal.podoapp.ui

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.R
import com.kasal.podoapp.PatientPhotoAdapter
import com.kasal.podoapp.data.Patient
import com.kasal.podoapp.data.PatientPhoto
import com.kasal.podoapp.data.PodologiaDatabase
import com.kasal.podoapp.openPatientHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PatientDetailActivity : AppCompatActivity() {

    private var patientId: Int = 0

    // Κουμπιά πλοήγησης
    private var btnVisitHistory: Button? = null
    private var btnPatientAppointments: Button? = null
    private var btnNewAppointment: Button? = null
    private var btnOpenHistory: Button? = null

    // Photos UI
    private lateinit var btnAddFromCamera: Button
    private lateinit var btnAddFromGallery: Button
    private lateinit var recyclerPatientPhotos: RecyclerView
    private lateinit var photoAdapter: PatientPhotoAdapter
    private var pendingCameraUri: Uri? = null

    // Permissions (images)
    private val requestPhotosPerms = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.all { it }) openGalleryPicker()
        else Toast.makeText(this, "Δεν δόθηκε άδεια για εικόνες", Toast.LENGTH_SHORT).show()
    }

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> if (uri != null) importAndSaveFromUri(uri) }

    private val takePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingCameraUri
        if (success && uri != null) savePhotoUri(uri) else if (uri != null) contentResolver.delete(uri, null, null)
        pendingCameraUri = null
    }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startCameraNow()
            else Toast.makeText(this, "Η κάμερα δεν έχει άδεια.", Toast.LENGTH_LONG).show()
        }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.values.all { it }) startCameraNow()
            else Toast.makeText(this, "Χρειάζονται άδειες κάμερας/αποθήκευσης.", Toast.LENGTH_LONG).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_detail)

        // Διαβάζουμε patientId
        val extraId = intent.getIntExtra("patientId", 0)
        patientId = if (extraId > 0) extraId else (intent.getSerializableExtra("patient") as? Patient)?.id ?: 0
        if (patientId <= 0) { Toast.makeText(this, "Άκυρο patientId", Toast.LENGTH_LONG).show(); finish(); return }

        bindViews()
        setPatientNameInTitleAndHeader(patientId)

        // Πλοήγηση (κουμπιά είναι προαιρετικά στο layout – αν υπάρχουν, βάζουμε listeners)
        btnVisitHistory?.setOnClickListener {
            val i = Intent(this, VisitListActivity::class.java).putExtra("patientId", patientId)
            startActivity(i)
        }
        btnPatientAppointments?.setOnClickListener {
            val i = Intent(this, AppointmentActivity::class.java).putExtra("patientId", patientId)
            startActivity(i)
        }
        btnNewAppointment?.setOnClickListener {
            val i = Intent(this, NewAppointmentActivity::class.java).putExtra("patientId", patientId)
            startActivity(i)
        }
        btnOpenHistory?.setOnClickListener { openPatientHistory(patientId) }

        // Photos
        photoAdapter = PatientPhotoAdapter(
            onClick = { photo ->
                val i = Intent(this, FullscreenPhotoActivity::class.java)
                    .putExtra("photoUri", photo.photoUri)
                    .putExtra("takenAtMillis", photo.takenAtMillis)
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

    /** Προαιρετικός header: ενημερώνεται αν υπάρχει στο layout */
    private fun setPatientNameInTitleAndHeader(id: Int) {
        val db = PodologiaDatabase.getDatabase(this)
        CoroutineScope(Dispatchers.IO).launch {
            val patient = db.patientDao().getPatientById(id)
            withContext(Dispatchers.Main) {
                val tvId = resources.getIdentifier("textPatientNameHeader", "id", packageName)
                val tv: TextView? = if (tvId != 0) findViewById(tvId) else null
                if (patient != null) {
                    val full = patient.fullName
                    supportActionBar?.title = full
                    supportActionBar?.subtitle = "ID #${patient.id}"
                    tv?.text = full
                } else {
                    val fallback = "Πελάτης #$id"
                    supportActionBar?.title = fallback
                    tv?.text = fallback
                }
            }
        }
    }

    private inline fun <reified T : View> findOptional(vararg ids: Int): T? {
        for (id in ids) {
            if (id != 0) {
                val v: View? = findViewById(id)
                if (v is T) return v
            }
        }
        return null
    }

    private fun bindViews() {
        // Προαιρετικά navigation buttons (αν υπάρχουν στο layout)
        btnVisitHistory        = findOptional(R.id.btnVisitHistory)
        btnPatientAppointments = findOptional(R.id.btnPatientAppointments)
        btnNewAppointment      = findOptional(R.id.btnNewAppointment)
        btnOpenHistory         = findOptional(R.id.btnOpenHistory)

        // Photos (υποχρεωτικά)
        btnAddFromCamera       = findViewById(R.id.btnAddFromCamera)
        btnAddFromGallery      = findViewById(R.id.btnAddFromGallery)
        recyclerPatientPhotos  = findViewById(R.id.recyclerPatientPhotos)
    }

    // ---------------- Photos ----------------

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

    private fun openGalleryPicker() { pickImage.launch("image/*") }

    private fun captureFromCamera() {
        if (Build.VERSION.SDK_INT >= 33) {
            val camGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            if (!camGranted) { requestCameraPermission.launch(Manifest.permission.CAMERA); return }
            startCameraNow(); return
        }
        if (Build.VERSION.SDK_INT in 29..32) {
            val needs = mutableListOf<String>()
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                needs += Manifest.permission.CAMERA
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                needs += Manifest.permission.READ_EXTERNAL_STORAGE
            if (needs.isNotEmpty()) { requestMultiplePermissions.launch(needs.toTypedArray()); return }
            startCameraNow(); return
        }
        val needs = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            needs += Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            needs += Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            needs += Manifest.permission.READ_EXTERNAL_STORAGE
        if (needs.isNotEmpty()) { requestMultiplePermissions.launch(needs.toTypedArray()); return }
        startCameraNow()
    }

    private fun startCameraNow() {
        val now = System.currentTimeMillis()
        val name = "podoapp_${patientId}_$now.jpg"
        val collection = if (Build.VERSION.SDK_INT >= 29)
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        else MediaStore.Images.Media.EXTERNAL_CONTENT_URI
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
        if (uri == null) { Toast.makeText(this, "Αποτυχία δημιουργίας αρχείου εικόνας", Toast.LENGTH_SHORT).show(); return }
        pendingCameraUri = uri
        takePicture.launch(uri)
    }

    private fun savePhotoUri(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            PodologiaDatabase.getDatabase(this@PatientDetailActivity)
                .patientPhotoDao()
                .insert(PatientPhoto(patientId = patientId, photoUri = uri.toString(), takenAtMillis = System.currentTimeMillis()))
            withContext(Dispatchers.Main) { Toast.makeText(this@PatientDetailActivity, "Προστέθηκε φωτογραφία", Toast.LENGTH_SHORT).show() }
        }
    }

    private fun importAndSaveFromUri(src: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            val dest = copyImageToAppAlbum(src)
            if (dest == null) { withContext(Dispatchers.Main) { Toast.makeText(this@PatientDetailActivity, "Αποτυχία εισαγωγής εικόνας", Toast.LENGTH_SHORT).show() }; return@launch }
            PodologiaDatabase.getDatabase(this@PatientDetailActivity)
                .patientPhotoDao()
                .insert(PatientPhoto(patientId = patientId, photoUri = dest.toString(), takenAtMillis = System.currentTimeMillis()))
            withContext(Dispatchers.Main) { Toast.makeText(this@PatientDetailActivity, "Η φωτογραφία προστέθηκε", Toast.LENGTH_SHORT).show() }
        }
    }

    private fun copyImageToAppAlbum(src: Uri): Uri? {
        return try {
            val now = System.currentTimeMillis()
            val name = "podoapp_${patientId}_$now.jpg"
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATE_TAKEN, now)
                if (Build.VERSION.SDK_INT >= 29) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PodoApp")
                    put(MediaStore.Images.Media.IS_PENDING, 0)
                }
            }
            val dest = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return null
            contentResolver.openInputStream(src)?.use { input ->
                contentResolver.openOutputStream(dest)?.use { output -> input.copyTo(output) } ?: return null
            } ?: return null
            dest
        } catch (_: Exception) { null }
    }

    private fun migrateOldPickerUrisIfAny() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = PodologiaDatabase.getDatabase(this@PatientDetailActivity)
            val list = db.patientPhotoDao().getPhotosForPatient(patientId)
            for (photo in list) {
                val u = Uri.parse(photo.photoUri)
                if (u.authority?.contains("photopicker") == true) {
                    val dest = copyImageToAppAlbum(u)
                    if (dest != null) db.patientPhotoDao().updateUri(photo.id, dest.toString())
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
            PodologiaDatabase.getDatabase(this@PatientDetailActivity).patientPhotoDao().delete(photo.id)
            runCatching { contentResolver.delete(Uri.parse(photo.photoUri), null, null) }
            withContext(Dispatchers.Main) { Toast.makeText(this@PatientDetailActivity, "Διαγράφηκε", Toast.LENGTH_SHORT).show() }
        }
    }
}
