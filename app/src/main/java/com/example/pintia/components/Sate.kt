package com.example.pintia.components

import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.pintia.R
import com.example.pintia.models.Punto

class SateliteMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val backgroundImageView: ImageView

    // Parámetros del archivo .jgw (ajusta estos valores a los de tu archivo real)
    private val pixelSizeX = 20.17541326219043
    private val pixelSizeY = -20.17541326219043
    private val originX = 455900.0
    private val originY = 4743700.0

    init {
        LayoutInflater.from(context).inflate(R.layout.activity_main_map, this, true)
        backgroundImageView = findViewById(R.id.satelite_background_image)
        setBackgroundFromDrawable()
    }

    private fun setBackgroundFromDrawable() {
        backgroundImageView.setImageResource(R.drawable.mapa) // Ajusta el recurso aquí
    }

    private fun geoToPixel(latitude: Double, longitude: Double): PointF {
        val pixelX = ((longitude - originX) / pixelSizeX).toFloat()
        val pixelY = ((originY - latitude) / -pixelSizeY).toFloat()
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
            val pointButton = ImageButton(context).apply {
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
            this.addView(pointButton)
        }
    }
}