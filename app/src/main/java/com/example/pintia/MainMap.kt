package com.example.pintia

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PointF
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.components.Header
import com.example.pintia.components.Leyenda
import com.example.pintia.models.Punto
import com.example.pintia.puntosPrincipales.EdificioUVaActivity
import com.example.pintia.puntosPrincipales.LasQuintanasActivity
import com.example.pintia.puntosPrincipales.LasRuedasActivity
import com.example.pintia.puntosPrincipales.MurallaAsedioActivity
import com.example.pintia.utils.TutorialStep
import com.example.pintia.utils.TutorialManager

import kotlin.math.abs

class MainMap : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_main_map)
        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.home)

        val button = header.findViewById<ImageButton>(R.id.back_button_header)
        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val btn_request = findViewById<LinearLayout>(R.id.reserva_btn)
        btn_request.setOnClickListener {
            val intent = Intent(this, RequestVisitActivity::class.java)
            startActivity(intent)
        }

        // Crear una lista de los IDs de los botones
        val buttonIds = listOf(R.id.button1, R.id.button2, R.id.button3, R.id.button4)

        // Crear una lista de actividades de destino para cada botón
        val targetActivities = listOf(
            LasQuintanasActivity::class.java,
            MurallaAsedioActivity::class.java,
            LasRuedasActivity::class.java,
            EdificioUVaActivity::class.java
        )

        // Iterar sobre los botones y asignarles su funcionalidad de navegación
        buttonIds.forEachIndexed { index, buttonId ->
            val button = findViewById<LinearLayout>(buttonId)

            // Asignar el listener para abrir la actividad correspondiente
            button.setOnClickListener {
                val intent = Intent(this, targetActivities[index])
                startActivity(intent)
            }
        }
    }

    // Método para verificar si es la primera vez que se muestra el tutorial
    private fun isFirstTimeTutorial(): Boolean {
        val preferences: SharedPreferences = getSharedPreferences("TutorialPreferences", Context.MODE_PRIVATE)
        return preferences.getBoolean("TutorialShown", true) // Por defecto, true (primera vez)
    }
}