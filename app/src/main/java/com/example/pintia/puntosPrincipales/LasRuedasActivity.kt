package com.example.pintia.puntosPrincipales

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.R
import com.example.pintia.components.Header

class LasRuedasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_las_ruedas)
        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.ruedas)
    }
}