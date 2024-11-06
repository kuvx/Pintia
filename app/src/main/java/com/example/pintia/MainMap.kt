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

    // Parámetros del archivo .jgw (ajusta estos valores a los de tu archivo real)
    private val pixelSizeX = 4.721207156775379
    private val pixelSizeY = -4.721207156775843
    private val originX = -467173.0910876456
    private val originY = 5105407.453628538

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

        // Crear la lista de puntos
        val points = listOf(
            Punto(
                latitude =  41.6227296884,
                longitude = -4.1695402615,
                icon = R.color.black, // Reemplaza con tu icono
                destinationActivity = InfoActivity::class.java // Actividad de destino para este punto
            ),/*
            Punto(
                latitude =  originY,
                longitude = originX,
                icon = R.drawable.round_button_selected_background, // Reemplaza con tu icono
                destinationActivity = InfoActivity::class.java // Actividad de destino para este punto
            ),
            *
            Punto(
                latitude = 4743600.0,
                longitude = 455930.0,
                icon = R.drawable.round_button_selected_background,
                destinationActivity = GalleryActivity::class.java
            )
            */
            // Añade más puntos según necesites
        )

        // Añadir los puntos al componente
        addPoints(points)


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

    private fun setBackgroundFromDrawable() {
        backgroundImageView.setImageResource(R.drawable.mapa) // Ajusta el recurso aquí
    }

    private fun geoToPixel(latitude: Double, longitude: Double): PointF {
        val pixelX = ((longitude - originX) / pixelSizeX).toFloat()
        val pixelY = ((originY - latitude) / abs(pixelSizeY)).toFloat()
        val a = PointF(pixelX, pixelY)
        println(a.x.toString() + " " + a.y)
        return PointF(pixelX, pixelY)
    }

    /**
     * Recibe una lista de objetos Punto y añade cada uno como un ImageButton en la posición calculada.
     */
    fun addPoints(points: List<Punto>) {
        points.forEach { point ->
            // Convierte las coordenadas geográficas a píxeles
            val position = geoToPixel(point.latitude, point.longitude)

            // Crea un ImageButton para representar el punto
            val pointButton = ImageButton(this).apply {
                setImageResource(point.icon) // Usa el ícono especificado en Punto
                layoutParams = LayoutParams(80, 80) // Tamaño del botón, ajusta según tu preferencia
                scaleType = ImageView.ScaleType.CENTER
                translationX = position.x
                translationY = position.y

                // Configura el click listener para redirigir a la actividad correspondiente
                setOnClickListener {
                    val intent = Intent(context, point.destinationActivity)
                    context.startActivity(intent)
                }
            }

            // Añadir el punto a la vista
            findViewById<RelativeLayout>(R.id.satelite_map_view).addView(pointButton)
        }
    }
}