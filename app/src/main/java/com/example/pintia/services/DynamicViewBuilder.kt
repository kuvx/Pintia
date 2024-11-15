package com.example.pintia.services

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import java.util.Locale

// Clase para representar el contenido (puede venir de un JSON)
data class ContentItem(
    val type: String, // Puede ser "text" o "image"
    val value: String // El texto o la URL de la imagen
)

object DynamicViewBuilder {
    fun loadContentFromJson(context: Context, fileName: String): List<ContentItem> {
        // Obtén el código del idioma actual del sistema (por ejemplo, "en", "es", etc.)
        val currentLanguage = Locale.getDefault().language

        // Define la ruta del archivo según el idioma
        println(currentLanguage)
        val fileName = "$currentLanguage/${fileName}"  // Por ejemplo, "en/mi_archivo.json"
        var jsonString: String? = null
        try {
            // Accede al archivo en assets
            val inputStream: InputStream = context.assets.open(fileName)
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
                    // Usar Glide para cargar la imagen desde la URL
                    Glide.with(container.context)
                        .load(item.value)
                        .into(imageView)

                    // Añadir el ImageView al contenedor
                    container.addView(imageView)
                }
            }
        }
    }

}