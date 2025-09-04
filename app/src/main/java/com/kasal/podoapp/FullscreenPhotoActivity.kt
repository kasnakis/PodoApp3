package com.kasal.podoapp.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kasal.podoapp.R
import java.text.SimpleDateFormat
import java.util.*

class FullscreenPhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_photo)

        val photoUri = intent.getStringExtra("photoUri")?.let { Uri.parse(it) }
        val takenAtMillis = intent.getLongExtra("takenAtMillis", 0)

        if (photoUri == null) {
            finish()
            return
        }

        // Corrected: Use a standard ImageView
        val photoView: ImageView = findViewById(R.id.photo_view)
        val tvTimestamp: TextView = findViewById(R.id.tvTimestamp)

        photoView.setImageURI(photoUri)

        if (takenAtMillis > 0) {
            val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvTimestamp.text = fmt.format(Date(takenAtMillis))
            tvTimestamp.visibility = View.VISIBLE
        } else {
            tvTimestamp.visibility = View.GONE
        }
    }
}