package com.example.pintia.models
import android.graphics.Color
import android.graphics.PointF
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.GalleryActivity
import com.example.pintia.puntosPrincipales.lasQuintanasViews.YacimientoInfoView

data class PoligonoColor(
    val puntos: List<PointF>, // Lista de puntos que forman el polígono
    val color: Int = Color.argb(128, 48, 156, 162),            // Color naranja Color.argb(128, 234, 151, 48)
    val destinationActivity: Class<out AppCompatActivity>? = YacimientoInfoView::class.java, // Clase de la actividad a redirigir
    val title : String
) {
    // Método para convertir los puntos en un Path que se pueda dibujar
    fun crearPath(): Path {
        val path = Path()
        if (puntos.isNotEmpty()) {
            path.moveTo(puntos[0].x, puntos[0].y)
            for (punto in puntos.drop(1)) {
                path.lineTo(punto.x, punto.y)
            }
            path.close()
        }
        return path
    }

    // Cálculo del centroide basado en los puntos
    fun getCentroide(): PointF {
        var sumX = 0f
        var sumY = 0f
        for (punto in puntos) {
            sumX += punto.x
            sumY += punto.y
        }
        val centerX = sumX / puntos.size
        val centerY = sumY / puntos.size
        return PointF(centerX, centerY)
    }
}
