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
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.example.pintia.R
import com.example.pintia.models.Punto
import com.example.pintia.puntosPrincipales.lasQuintanasViews.YacimientoInfoFragment
import com.example.pintia.services.model3d.Model3D
import com.example.pintia.utils.ImageInfoWindow
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.getstream.photoview.PhotoView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.FileWriter
import java.io.File
import java.io.IOException
import java.io.InputStream

// Clase para representar el contenido (puede venir de un JSON)
data class ContentItem(
    val type: String, // Puede ser "text" o "image"
    val value: String,
    val latitude: Double,
    val longitude: Double,
    val icon: Int = R.drawable.point,
    val action: Class<out AppCompatActivity>?
)

object DynamicViewBuilder {
    fun loadContentFromJson(
        context: Context,
        fileName: String,
        infoFlag: Boolean
    ): List<ContentItem> {
        // Obtén el código del idioma actual del sistema (por ejemplo, "en", "es", etc.)
        //val currentLanguage = Locale.getDefault().language
        val currentLanguage = context.resources?.configuration?.locales?.get(0)?.language

        // Define la ruta del archivo según el idioma
        println(currentLanguage)
        val fileRoute =
            if (infoFlag) "$currentLanguage/${fileName}" else fileName    // Por ejemplo, "en/mi_archivo.json"
        var jsonString: String? = null
        try {
            // Accede al archivo en assets

            val inputStream: InputStream = context.assets.open(fileRoute)
            jsonString =
                inputStream.bufferedReader().use { it.readText() }  // Lee el archivo como texto
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
        return convertContentItemList(jsonString)

    }

    fun convertContentItemList(jsonString: String?): List<ContentItem> {
        // Convierte el JSON en una lista de ContentItem
        val gson = Gson()
        val listType = object : TypeToken<List<ContentItem>>() {}.type
        return gson.fromJson(jsonString, listType)
    }

    // Método para construir dinámicamente la vista
    fun populateDynamicDescription(
        layout: RelativeLayout,
        seccion: String,
        container: LinearLayout,
        contentItems: List<ContentItem>
    ): String {
        var salida = seccion
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
                    // Añadir el contenido al audio
                    salida = "$salida\n ${item.value}\n"
                }

                "image" -> {
                    // Crear un ImageView para la imagen
                    val imageView = PhotoView(container.context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 16, 0, 16) // Márgenes opcionales
                        }
                        scaleType = ImageView.ScaleType.CENTER_CROP // Ajustar imagen al tamaño
                        adjustViewBounds = true
                    }
                    imageView.setScale(1.0f, true)
                    imageView.maximumScale = 10.0f // Escala máxima
                    imageView.minimumScale = 1.0f  // Escala mínima

                    // Usar Glide para cargar la imagen desde la ruta
                    try {
                        val urlPattern = "^(https?://).*$".toRegex(RegexOption.IGNORE_CASE)
                        val typeImage = urlPattern.matches(item.value)
                        if (!typeImage) {
                            val assetManager = container.context.assets
                            val inputStream = assetManager.open(item.value)
                            val bitmap = BitmapFactory.decodeStream(inputStream)

                            Glide.with(container.context)
                                .load(bitmap)
                                .into(imageView)
                        } else {
                            println("Https")
                            Glide.with(container.context)
                                .load(item.value)
                                .into(imageView)
                        }

                    } catch (e: Exception) {
                        Log.d("Error_Image", e.toString())
                    }

                    // Añadir el ImageView al contenedor
                    container.addView(imageView)
                }

                "model3D" -> {
                    // Crear un ImageView para la imagen
                    val htmlContent = item.value.trimIndent()
                    val modelView = Model3D.getWebChromeClient(layout, container)
                    modelView.loadData(htmlContent, "text/html", "UTF-8")
                    // Añadir el ImageView al contenedor
                    container.addView(modelView)

                }

            }
        }
        return salida
    }

    fun loadMarkersCache(context: Context, fileTitle: String): List<ContentItem> {
        var jsonString: String? = null
        try {
            // Accede al archivo en cache
            Log.d("LoadMakers", context.cacheDir.toString())
            val file = File(context.cacheDir, fileTitle)
            if (!file.exists()) {
                file.createNewFile()
                file.writeText("[]")
            }
            jsonString = file.bufferedReader().use { reader ->
                reader.readText()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("Error_LoadMakers", e.toString())
        }
        return convertContentItemList(jsonString)
    }

    fun removeMarkerOfFile(context: Context, fileTitle: String) {
        var listMarkers = loadMarkersCache(context, "photos_marker.json").toMutableList()
        listMarkers.removeIf { it.value == fileTitle }
        saveOnFile(context, listMarkers, "photos_marker.json")

    }

    // Guardar un marcador en un archivo JSON
    fun saveMarkersToFile(marker: Marker, context: Context) {
        val fileTitle: String = "photos_marker.json"
        // Convierte los marcadores en objetos MarkerData
        var markerDetails = ContentItem(
            type = "marker",
            value = marker.snippet,
            latitude = marker.position.latitude,
            longitude = marker.position.longitude,
            action = null
        )
        val listMarkers = loadMarkersCache(context, fileTitle).toMutableList()

        listMarkers.add(markerDetails)
        saveOnFile(context, listMarkers, fileTitle)
    }

    fun saveOnFile(context: Context, listMarkers: List<ContentItem>, file_title: String) {
        // Convierte a JSON
        val gson = Gson()
        val json = gson.toJson(listMarkers)

        // Escribe el JSON en un archivo en almacenamiento interno
        val file = File(context.cacheDir, file_title)
        FileWriter(file).use { writer ->
            writer.write(json)
        }
    }

    fun populateDynamicPoints(
        contentItems: List<ContentItem>,
        path: String
    ): List<Punto> {
        val listaSalida: MutableList<Punto> = mutableListOf()
        for (item in contentItems) {
            when (item.type) {
                "point" -> {
                    // Crear un TextView para la descripción
                    try {
                        val punto = Punto(
                            item.value,
                            item.latitude,
                            item.longitude,
                            item.icon,
                            YacimientoInfoFragment.newInstance(item.value, path)
                        )
                        Log.d("Point", "${punto.title}: ${punto.latitude}")
                        // Añadir el TextView al contenedor
                        listaSalida.add(punto)
                    } catch (e: Exception) {
                        Log.d("Error Point", e.toString())
                    }
                }
            }
        }
        Log.d("ListaPuntos", listaSalida.toString())
        return listaSalida
    }

    fun populateDynamicMarkers(context: Context, mapView: MapView): List<Marker> {
        val contentItems = loadMarkersCache(context, "photos_marker.json")
        Log.d("PuntosAñadidos", contentItems.toMutableList().toString())
        val listaSalida: MutableList<Marker> = mutableListOf()
        for (item in contentItems) {
            when (item.type) {
                "marker" -> {
                    // Crear un TextView para la descripción
                    println(item.value)
                    try {
                        val marker = Marker(mapView)
                        marker.position = GeoPoint(item.latitude, item.longitude)
                        marker.snippet = item.value
                        marker.icon = ResourcesCompat.getDrawable(context.resources, R.drawable.point, null)
                        // Crea y asigna la ventana de información personalizada
                        val infoWindow = ImageInfoWindow(mapView)
                        marker.infoWindow = infoWindow
                        // Añadir el TextView al contenedor
                        listaSalida.add(marker)
                    } catch (e: Exception) {
                        Log.d("Error Marker", e.toString())
                    }
                }
            }
        }
        Log.d("ListaMarkers", listaSalida.toString())
        return listaSalida
    }

    fun generateDrawableWithText(
        context: Context,
        backgroundDrawable: Drawable,
        text: String
    ): Drawable {
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

    fun pueblaActivity(
        layout: RelativeLayout,
        context: Context,
        path: String,
        titulo_cod: String,
        ttsManager: TTSManager
    ) {
        val contentItems = loadContentFromJson(context, "${path}/data_${titulo_cod}.json", true)

        val dynamicContainer = layout.findViewById<LinearLayout>(R.id.dynamic_description_container)
        val tituloTTL = populateDynamicDescription(
            layout,
            context.getString(R.string.description),
            dynamicContainer,
            contentItems
        )

        val contentItems_moreInfo =
            loadContentFromJson(context, "${path}/data_${titulo_cod}_more.json", true)
        Log.d("JSONView", contentItems.toString())

        val dynamicContainer_more =
            layout.findViewById<LinearLayout>(R.id.dynamic_more_info_container)
        val moreTTL = populateDynamicDescription(
            layout,
            context.getString(R.string.more_info_title),
            dynamicContainer_more,
            contentItems_moreInfo
        )

        // Inicializar el manejador de botones de audio
        var audioButtonHandler: AudioButtonHandler = AudioButtonHandler(context, ttsManager)

        // Configuración de la velocidad de audio
        val speedSelector: Button = layout.findViewById(R.id.speed_selector)
        val speedSelector_2: Button = layout.findViewById(R.id.speed_selector_2)

        // Configurar botones de audio
        val audioView1 = layout.findViewById<ImageButton>(R.id.audio_player)
        val audioView2 = layout.findViewById<ImageButton>(R.id.audio_player_2)

        audioButtonHandler.changeSpeed(speedSelector, audioView1)
        audioButtonHandler.changeSpeed(speedSelector_2, audioView2)

        // Configurar cada botón con su respectivo texto
        audioButtonHandler.setupAudioButton(audioView1, tituloTTL, speedSelector)
        audioButtonHandler.setupAudioButton(audioView2, moreTTL, speedSelector_2)
    }


}