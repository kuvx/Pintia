package com.example.pintia

import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mainLayout = findViewById<RelativeLayout>(R.id.main)
        mainLayout.setOnClickListener {
            val intent = Intent(this, MainMap::class.java)
            startActivity(intent)
        }
        // TODO agregar un temporizador
    }
}