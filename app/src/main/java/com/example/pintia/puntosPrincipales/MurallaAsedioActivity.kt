package com.example.pintia.puntosPrincipales

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.R

class MurallaAsedioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_muralla_asedio)

        // Configurar el header
        val header = findViewById<com.example.pintia.components.Header>(R.id.header)
        header.title = getString(R.string.muralla)

        // Referencia al overlay
        val infoOverlay = findViewById<View>(R.id.info_overlay)

        // Referencia al botón de información
        val infoButton = findViewById<View>(R.id.info_button)

        // Mostrar el overlay al pulsar el botón de información
        infoButton.setOnClickListener {
            infoOverlay.visibility = View.VISIBLE // Muestra el overlay
        }

        // Ocultar el overlay al hacer clic en cualquier parte del mismo
        infoOverlay.setOnClickListener {
            infoOverlay.visibility = View.GONE // Oculta el overlay
        }
    }
}
