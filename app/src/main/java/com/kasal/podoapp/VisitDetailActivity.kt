package com.kasal.podoapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class VisitDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_detail)

        val tv = findViewById<TextView>(R.id.textVisitDetail)
        val visitId = intent?.getLongExtra("visitId", -1L) ?: -1L
        tv.text = if (visitId > 0) {
            "Visit detail (ID: $visitId)\n— placeholder screen —"
        } else {
            "Visit detail\n— placeholder screen —"
        }
    }
}
