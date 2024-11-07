package com.example.pintia

import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout.LayoutParams
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.components.Header
import com.example.pintia.components.Leyenda
import com.example.pintia.models.Punto
import kotlin.math.abs

class MainMap : AppCompatActivity() {

    private val backgroundImageView: ImageView by lazy {
        findViewById(R.id.satelite_background_image)
    }

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
        setBackgroundFromDrawable()

    }

    private fun setBackgroundFromDrawable() {
        backgroundImageView.setImageResource(R.drawable.mapa) // Ajusta el recurso aquí
    }
}