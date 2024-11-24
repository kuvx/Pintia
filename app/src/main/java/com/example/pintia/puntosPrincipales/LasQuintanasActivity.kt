package com.example.pintia.puntosPrincipales

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.R
import com.example.pintia.components.Header
import com.example.pintia.components.Leyenda
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Region
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Toast
import com.example.pintia.GalleryActivity
import com.example.pintia.models.PoligonoColor
import com.example.pintia.models.Punto
import com.example.pintia.services.OnPoligonoTouchListener
import com.example.pintia.services.OverlayView

class LasQuintanasActivity : AppCompatActivity(), OnPoligonoTouchListener {

    // Crear un Path para definir la forma irregular
    private val zonaIrregularPath = Path()
    private val zonaIrregularRegion = Region()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_las_quintanas)
        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.quinanas)
        header.onBackButtonClick = {
            finish()
        }

        val overlayView: OverlayView = findViewById(R.id.overlayView)

        val poligonos = listOf(

            PoligonoColor(
                puntos = listOf(PointF(224f, 535f), PointF(580f, 508f), PointF(722f, 704f), PointF(224f, 729f)),
                title = getString(R.string.sigloIV)
            ),
            PoligonoColor(
                puntos = listOf(PointF(65f, 753f), PointF(0f, 1400f), PointF(891f, 923f), PointF(722f, 720f)),
                title = getString(R.string.sigloI)
            ),
            PoligonoColor(
                puntos = listOf(PointF(0f, 1430f), PointF(0f, 1654f), PointF(1040f, 1654f), PointF(1040f, 1080f), PointF(911f, 953f)),
                title = getString(R.string.sigloII)
            ),
            PoligonoColor(
                puntos = listOf(PointF(723f, 635f), PointF(855f, 597f), PointF(1040f, 700f), PointF(1040f, 933F)),
                title = getString(R.string.sigloIII)
            ),
            PoligonoColor(
                puntos = listOf(PointF(224f, 355f), PointF(580f, 355f), PointF(660f, 440f), PointF(224f, 460f)),
                title = getString(R.string.pozo)
            )
        )

        // Asigna la lista de polígonos a OverlayView para que los dibuje
        overlayView.poligonos = poligonos
        // Asigna el listener
        overlayView.onPoligonoTouchListener = this

    }
    // Implementación del listener para cuando se toca un polígono
    override fun onPoligonoTouched(poligono: PoligonoColor) {
        Toast.makeText(this, "Tocaste un polígono: ${poligono.title}", Toast.LENGTH_SHORT).show()
        if (poligono.destinationActivity != null){
            val intent = Intent(this, poligono.destinationActivity)
            intent.putExtra("title", poligono.title)
            intent.putExtra("path", "quintana")
            startActivity(intent)
        }
    }
}