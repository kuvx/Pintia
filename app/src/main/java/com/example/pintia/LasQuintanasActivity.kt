package com.example.pintia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.components.Header

class LasQuintanasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.quinanas)
    }
}