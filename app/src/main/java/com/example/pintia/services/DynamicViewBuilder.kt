package com.example.pintia.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pintia.R
import com.example.pintia.models.Punto
import com.example.pintia.puntosPrincipales.lasQuintanasViews.YacimientoInfoView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import java.util.Locale

// Clase para representar el contenido (puede venir de un JSON)
data class ContentItem(
    val type: String, // Puede ser "text" o "image"
    val value: String,
    val latitude: Double,
    val longitude: Double,
    val icon: Int = R.drawable.point,
    val action : Class<out AppCompatActivity>
)

object DynamicViewBuilder {
    fun loadContentFromJson(context: Context, fileName: String, infoFlag :Boolean): List<ContentItem> {
        // Obtén el código del idioma actual del sistema (por ejemplo, "en", "es", etc.)
        //val currentLanguage = Locale.getDefault().language
        val currentLanguage = context.resources?.configuration?.locales?.get(0)?.language

        // Define la ruta del archivo según el idioma
        println(currentLanguage)
        val fileRoute = if(infoFlag) "$currentLanguage/${fileName}" else fileName    // Por ejemplo, "en/mi_archivo.json"
        var jsonString: String? = null
        try {
            // Accede al archivo en assets
            val inputStream: InputStream = context.assets.open(fileRoute)
            jsonString = inputStream.bufferedReader().use { it.readText() }  // Lee el archivo como texto
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

        // Convierte el JSON en una lista de ContentItem
        val gson = Gson()
        val listType = object : TypeToken<List<ContentItem>>() {}.type
        return gson.fromJson(jsonString, listType)
    }

    // Método para construir dinámicamente la vista
    fun populateDynamicDescription(container: LinearLayout, contentItems: List<ContentItem>) {
        for (item in contentItems) {
            when (item.type) {
                "text" -> {
                    // Crear un TextView para la descripción
                    val textView = TextView(container.context).apply {
                        text = item.value
                        textSize = 16f // Tamaño de texto
                        setTextColor(container.context.getColor(android.R.color.white)) // Color blanco
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 16, 0, 16) // Márgenes opcionales
                        }
                    }
                    // Añadir el TextView al contenedor
                    container.addView(textView)
                }
                "image" -> {
                    // Crear un ImageView para la imagen
                    val imageView = ImageView(container.context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 16, 0, 16) // Márgenes opcionales
                        }
                        scaleType = ImageView.ScaleType.CENTER_CROP // Ajustar imagen al tamaño
                        adjustViewBounds = true
                    }
                    // Usar Glide para cargar la imagen desde la ruta
                    try {
                        val assetManager = container.context.assets
                        val inputStream = assetManager.open(item.value)

                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        Glide.with(container.context)
                            .load(bitmap)
                            .into(imageView)

                    }catch (e:Exception){
                        Log.d("Error_Image",e.toString())
                    }

                    // Añadir el ImageView al contenedor
                    container.addView(imageView)
                }
            }
        }
    }

    fun populateDynamicPoints(contentItems: List<ContentItem>) : List<Punto> {
        val listaSalida : MutableList<Punto> = mutableListOf()
        for (item in contentItems) {
            when (item.type) {
                "point" -> {
                    // Crear un TextView para la descripción
                    try {
                        val punto = Punto(item.value, item.latitude, item.longitude, item.icon,YacimientoInfoView::class.java)
                        Log.d("Point","${punto.title}: ${punto.latitude}")
                        // Añadir el TextView al contenedor
                        listaSalida.add(punto)
                    }catch (e: Exception){
                        Log.d("Error Point", e.toString())
                    }
                }
            }
        }
        Log.d("ListaPuntos",listaSalida.toString())
        return listaSalida
    }

    fun generateDrawableWithText(context: Context, backgroundDrawable: Drawable, text: String): Drawable {
        // Crear un bitmap a partir del fondo (en este caso el drawable)
        val width = backgroundDrawable.intrinsicWidth
        val height = backgroundDrawable.intrinsicHeight

        // Si el drawable no tiene un tamaño, usamos un tamaño predeterminado
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Dibujar el fondo sobre el canvas
        backgroundDrawable.setBounds(0, 0, width, height)
        backgroundDrawable.draw(canvas)

        // Crear una capa con el texto sobre el fondo
        val paint = Paint()
        paint.color = Color.BLACK  // Puedes cambiar el color del texto aquí
        paint.textSize = 40f  // Tamaño de texto
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.CENTER

        // Definir la posición del texto
        val xPos = width / 2f
        val yPos = (height / 2f - (paint.descent() + paint.ascent()) / 2)

        // Dibujar el texto sobre el fondo
        canvas.drawText(text, xPos, yPos, paint)

        // Convertir el bitmap a un drawable
        val finalDrawable = BitmapDrawable(context.resources, bitmap)

        return finalDrawable
    }

}