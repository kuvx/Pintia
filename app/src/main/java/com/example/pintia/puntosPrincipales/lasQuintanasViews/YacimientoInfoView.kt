package com.example.pintia.puntosPrincipales.lasQuintanasViews

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pintia.R
import com.example.pintia.components.Header

class YacimientoInfoView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_yacimiento_info_view)
        val header = findViewById<Header>(R.id.header)
        // Recuperar los datos del Intent
        header.title = intent.getStringExtra("title").toString()
    }
}