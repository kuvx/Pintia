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
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.example.pintia.R
import com.example.pintia.models.ContentItem
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
import java.io.*

private const val fil_name_cache:String ="photos_marker.json"

/**
 * Clase para representar el contenido de los ficheros json y transformarlo en los diferentes elementos de la app
 * type : {
 *  text => contenido de las vistas de informacion
 *  image => imagenes {http/https| locales} q se muestran en las vistas de informacion
 *  modelo3D => contenido html <iframe></iframe> con modelos 3D
 *  pregunta => corresponde con los retos o preguntas, en el caso de la pregunta separa la respuesta mediante '|' (pregunta?|respuesta)
 *  point => corresponde con un punto del mapa ya sea del cementerio o para futuras incluiones
 * }
 */
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
            if (infoFlag) "$currentLanguage/${fileName}" else fileName
        var jsonString: String? = null
        try {
            // Accede al archivo en assets

            val inputStream: InputStream = context.assets.open(fileRoute)
            jsonString =
                inputStream.bufferedReader().use { it.readText() }  // Lee el archivo como texto
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return convertContentItemList(jsonString)

    }

    private fun convertContentItemList(jsonString: String?): List<ContentItem> {
        // Convierte el JSON en una lista de ContentItem
        val gson = Gson()
        val listType = object : TypeToken<List<ContentItem>>() {}.type
        return gson.fromJson(jsonString, listType)
    }

    // Método para construir dinámicamente la vista
    private fun populateDynamicDescription(
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
                    val modelView = Model3D.getWebChromeClient(layout)
                    modelView.loadData(htmlContent, "text/html", "UTF-8")
                    // Añadir el ImageView al contenedor
                    container.addView(modelView)

                }

                "pregunta" -> {
                    // Crear un TextView para el reto
                    var texto = "Reto!!"
                    // Es la regunta y la respuesta q se diferencian en la '|'
                    val cadenas = item.value.split("|")
                    val questionView = TextView(container.context).apply {
                        texto = "${texto}\n ${cadenas[0]}"
                        text = texto //Corresponde a la pregunta
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
                    container.addView(questionView)

                    //si en vez de ser preguntas son retos visuales
                    if(cadenas.size ==2){
                        // Crear un TextView para el reto
                        val answerView = TextView(container.context).apply {
                            text = cadenas[1]
                            textSize = 16f // Tamaño de texto
                            visibility = View.GONE
                            setTextColor(container.context.getColor(android.R.color.white)) // Color blanco
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(0, 16, 0, 16) // Márgenes opcionales
                            }
                        }
                        // Añadir el TextView al contenedor
                        container.addView(answerView)

                        val questionBtn = Button(container.context).apply{
                            text = context.getString(R.string.showMore)
                            setBackgroundResource(R.drawable.round_button_map)
                            setTextColor(ContextCompat.getColor(context, R.color.on_surface))
                            layoutParams =LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                            ).apply{
                                setMargins(16,16,16,16)
                                gravity = Gravity.CENTER
                            }
                            setPadding(16, 16,16,16)
                            isAllCaps = false //Texto sin mayusculas automaticas
                            setOnClickListener{
                                // Alternar visibilidad de la respuesta
                                if (answerView.visibility == View.GONE) {
                                    answerView.visibility = View.VISIBLE
                                    this.text = context.getString(R.string.showLess)
                                } else {
                                    answerView.visibility = View.GONE
                                    this.text = context.getString(R.string.showMore)
                                }
                            }
                        }

                        // Añadir el btn al contenedor
                        container.addView(questionBtn)
                    }

                    // Añadir el contenido al audio
                    salida = "$salida\n ${item.value}\n"
                }

            }
        }
        return salida
    }

    private fun loadMarkersCache(context: Context, fileTitle: String): List<ContentItem> {
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
        val listMarkers = loadMarkersCache(context, fil_name_cache).toMutableList()
        listMarkers.removeIf { it.value == fileTitle }
        saveOnFile(context, listMarkers)

    }

    // Guardar un marcador en un archivo JSON
    fun saveMarkersToFile(marker: Marker, context: Context) {
        // Convierte los marcadores en objetos MarkerData
        val markerDetails = ContentItem(
            type = "marker",
            value = marker.snippet,
            latitude = marker.position.latitude,
            longitude = marker.position.longitude,
            action = null
        )
        val listMarkers = loadMarkersCache(context, fil_name_cache).toMutableList()

        listMarkers.add(markerDetails)
        saveOnFile(context, listMarkers)
    }

    private fun saveOnFile(context: Context, listMarkers: List<ContentItem>) {
        // Convierte a JSON
        val gson = Gson()
        val json = gson.toJson(listMarkers)

        // Escribe el JSON en un archivo en almacenamiento interno
        val file = File(context.cacheDir, fil_name_cache)
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
        tituloCod: String,
        ttsManager: TTSManager
    ) {
        val contentItems = loadContentFromJson(context, "${path}/data_${tituloCod}.json", true)

        val dynamicContainer = layout.findViewById<LinearLayout>(R.id.dynamic_description_container)
        val tituloTTL = populateDynamicDescription(
            layout,
            context.getString(R.string.description),
            dynamicContainer,
            contentItems
        )

        val contentItemsMoreInfo =
            loadContentFromJson(context, "${path}/data_${tituloCod}_more.json", true)
        Log.d("JSONView", contentItems.toString())

        val dynamicContainerMore = layout.findViewById<LinearLayout>(R.id.dynamic_more_info_container)
        val moreTTL = populateDynamicDescription(
            layout,
            context.getString(R.string.more_info_title),
            dynamicContainerMore,
            contentItemsMoreInfo
        )

        // Inicializar el manejador de botones de audio
        val audioButtonHandler = AudioButtonHandler(context, ttsManager)

        // Configuración de la velocidad de audio
        val speedSelector: Button = layout.findViewById(R.id.speed_selector)
        val speedSelector2: Button = layout.findViewById(R.id.speed_selector_2)

        // Configurar botones de audio
        val audioView1 = layout.findViewById<ImageButton>(R.id.audio_player)
        val audioView2 = layout.findViewById<ImageButton>(R.id.audio_player_2)

        audioButtonHandler.changeSpeed(speedSelector, audioView1)
        audioButtonHandler.changeSpeed(speedSelector2, audioView2)

        // Configurar cada botón con su respectivo texto
        audioButtonHandler.setupAudioButton(audioView1, tituloTTL, speedSelector)
        audioButtonHandler.setupAudioButton(audioView2, moreTTL, speedSelector2)
    }


}