package com.example.pintia.puntosPrincipales

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.R
import com.example.pintia.components.Header
import com.example.pintia.components.Leyenda
import com.example.pintia.models.Punto
import com.example.pintia.puntosPrincipales.lasQuintanasViews.YacimientoInfoView
import com.example.pintia.services.DynamicViewBuilder.generateDrawableWithText
import com.example.pintia.services.DynamicViewBuilder.loadContentFromJson
import com.example.pintia.services.DynamicViewBuilder.populateDynamicPoints
import com.google.zxing.integration.android.IntentIntegrator
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*

class LasRuedasActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private val API_token = "pk.eyJ1IjoicGludGlhcHJveWVjdDI0IiwiYSI6ImNtMzdqNnNlaTA5emIybHF1NGU2OXI3Y2MifQ.4VtNOpGHNw88xqol5bl7pA"
    //Coordenadas Ruedas
    private var latitud=41.617962
    private var longitud=-4.169421


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_las_ruedas)
        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.app_name)

        val scanButton: ImageButton = findViewById(R.id.scanButton)
        scanButton.setOnClickListener { initScanner() }

        // Inicializa la configuración de OSMDroid
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))

        // Obtén una referencia al MapView
        mapView = findViewById(R.id.mapView)

        // Configura Mapbox como Tile Source
        mapView.setTileSource(mapboxTileSource)

        // Habilitar el zoom y el desplazamiento
        mapView.setMultiTouchControls(true)  // Permite hacer zoom y mover el mapa con gestos

        // Configura la cámara y la posición inicial
        val mapController: IMapController = mapView.controller

        mapController.setZoom(18)

        mapController.setCenter(GeoPoint(latitud, longitud))  // Pintia

        val contentItems = loadContentFromJson(this, "points_ruedas.json", false)
        Log.d("JSONView", contentItems.toString())
        val puntos = populateDynamicPoints(contentItems)
        Log.d("POINTs", puntos.toString())


        val leyenda = findViewById<Leyenda>(R.id.leyenda_main)
        val puntoMap = puntos.associateBy { it.title }
        leyenda.setMap(puntoMap)

        // Crea y configura los marcadores dinámicamente a partir de la lista de puntos
        puntos.forEachIndexed { index, punto ->
            val marker = Marker(mapView)
            marker.position = GeoPoint(punto.latitude, punto.longitude)
            marker.title = punto.title
            marker.icon = generateDrawableWithText(this,resources.getDrawable(R.drawable.point), (index).toString())


            // Configura el listener de clic para cada marcador
            marker.setOnMarkerClickListener { _, _ ->
                if (index!= 0 && index != 18){
                    val intent = Intent(this, punto.destinationActivity)
                    intent.putExtra("title", punto.title)
                    intent.putExtra("path", "ruedas")
                    startActivity(intent)
                    true
                }
                // Mostrar mensaje Toast (opcional)
                Toast.makeText(this, "Marcador clickeado: ${punto.title}", Toast.LENGTH_SHORT).show()
                true // Indica que el evento ha sido manejado

            }

            // Agrega el marcador al mapa
            mapView.overlays.add(marker)
            println("img")
        }

    }
    // Define la fuente de tiles personalizados para Mapbox
    private val mapboxTileSource = object : OnlineTileSourceBase(
        "Mapbox", // Nombre del tile source
        1, 22, 512, ".png", // Zoom mínimo, máximo, tamaño de tile, extensión
        arrayOf("https://api.mapbox.com/v4/mapbox.satellite/")
    ) {
        override fun getTileURLString(pMapTileIndex: Long): String {
            val zoom = MapTileIndex.getZoom(pMapTileIndex)
            val x = MapTileIndex.getX(pMapTileIndex)
            val y = MapTileIndex.getY(pMapTileIndex)
            return "$baseUrl$zoom/$x/$y.png?access_token=${API_token}"
        }
    }

    private fun initScanner() {
        val integrator = IntentIntegrator(this).apply{
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            setPrompt("Escanea un código QR")
            setOrientationLocked(true)
            setBeepEnabled(true)
        }
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Escaneado: ${result.contents}", Toast.LENGTH_LONG).show()
                val scannedUrl = result.contents
                if (isValidHttpsUrl(scannedUrl)) {
                    openUrlInBrowser(scannedUrl)
                } else {
                    Toast.makeText(this, "URL no válida o no es HTTPS", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun isValidHttpsUrl(url: String): Boolean {
        return try {
            val uri = Uri.parse(url)
            uri.scheme?.equals("https", ignoreCase = true) == true
        } catch (e: Exception) {
            false
        }
    }

    private fun openUrlInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        // Aquí puedes poner cualquier lógica que se deba inicializar antes de que el usuario vea la actividad.
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()  // Reanuda el mapa cuando la actividad se vuelve interactiva
    }

    override fun onPause() {
        mapView.onPause()  // Suspende el mapa cuando la actividad deja de ser interactiva
        super.onPause()
    }
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDetach()  // Esto asegura que el mapa se destruya al salir de la actividad
        mapView.overlayManager.clear()
    }
}
