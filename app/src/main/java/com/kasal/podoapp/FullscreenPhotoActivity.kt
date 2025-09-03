package com.kasal.podoapp.ui

import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kasal.podoapp.R
import com.kasal.podoapp.data.PodologiaDatabase
import com.kasal.podoapp.ui.widgets.ZoomableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class FullscreenPhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_photo)

        val photoView: ZoomableImageView = findViewById(R.id.photoView)
        val tvInfo: TextView = findViewById(R.id.tvInfo)
        val btnBack: ImageButton = findViewById(R.id.btnBack)

        val uriStr = intent.getStringExtra("photoUri")
        val takenAt = intent.getLongExtra("takenAtMillis", 0L)
        val photoId = intent.getIntExtra("photoId", 0)       // 👈 περνιέται από το click
        val patientId = intent.getIntExtra("patientId", 0)   // 👈 προαιρετικά

        if (takenAt > 0) {
            val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvInfo.text = fmt.format(Date(takenAt))
        } else tvInfo.text = ""

        if (uriStr != null) {
            val uri = Uri.parse(uriStr)
            try {
                photoView.setImageURI(uri)
            } catch (se: SecurityException) {
                Toast.makeText(this, "Η φωτογραφία δεν είναι πλέον διαθέσιμη.", Toast.LENGTH_LONG).show()
                // Προαιρετικά καθάρισε τη χαλασμένη εγγραφή για να φύγει από τη λίστα
                if (photoId > 0) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        PodologiaDatabase.getDatabase(this@FullscreenPhotoActivity)
                            .patientPhotoDao()
                            .delete(photoId)
                        withContext(Dispatchers.Main) { finish() }
                    }
                } else {
                    finish()
                }
                return
            }
        } else {
            finish(); return
        }

        btnBack.setOnClickListener { finish() }
    }
}
