package com.example.pintia.services

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Region
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.pintia.R
import com.example.pintia.models.PoligonoColor

class OverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Lista de polígonos de colores
    var poligonos: List<PoligonoColor> = listOf()
        set(value) {
            field = value
            invalidate() // Redibuja la vista cuando se actualiza la lista de polígonos
        }

    private val paint = Paint().apply {
        style = Paint.Style.FILL // Relleno del polígono
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }

    // Listener para detectar toques en los polígonos
    var onPoligonoTouchListener: OnPoligonoTouchListener? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Dibujar cada polígono con su color
        poligonos.forEach { poligonoColor ->
            paint.color = poligonoColor.color // Asignar color
            val path = poligonoColor.crearPath()

            canvas.drawPath(path, paint) // Dibujar el polígono en el Path

            val center= poligonoColor.getCentroide()
            // Dibuja el número o texto dentro del círculo
            canvas.drawText(poligonoColor.title, center.x, center.y + textPaint.textSize / 3, textPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            // Convertir cada polígono a una región y verificar si el punto está dentro de la región
            for (poligonoColor in poligonos) {
                val path = poligonoColor.crearPath()

                // Crear una región basada en el path del polígono
                val region = Region()
                val bounds = RectF()
                path.computeBounds(bounds, true)
                region.setPath(
                    path,
                    Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt())
                )

                // Verificar si el punto (x, y) del evento está dentro de la región
                if (region.contains(event.x.toInt(), event.y.toInt())) {
                    // Invocar el listener
                    onPoligonoTouchListener?.onPoligonoTouched(poligonoColor)
                    return true
                }
            }
        }
        return false
    }
}