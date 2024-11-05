package com.example.pintia

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.components.Header
import com.example.pintia.components.Leyenda

class MainMap : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_map)
        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.home)

        val button = header.findViewById<ImageButton>(R.id.back_button_header)
        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val leyenda = findViewById<Leyenda>(R.id.leyenda_main)
        leyenda.myMap =
            mapOf(
                Pair("-- Prueba --", MainActivity::class.java),
                Pair("-- Prueba 2 --", RequestVisitActivity::class.java),
                Pair("Punto de partida", null),
                Pair("Ruedas", null),
                Pair("Muralla", null),
                Pair("Catapulta", null),
                Pair("Yacimiento", null),
                )
    }
}