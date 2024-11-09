package com.example.pintia.puntosPrincipales

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.pintia.R
import com.example.pintia.components.Header
import com.example.pintia.components.Leyenda
import com.example.pintia.models.Punto
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
    private var latitud=41.6169600023
    private var longitud=-4.1691941788


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_las_ruedas)
        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.app_name)

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

        mapController.setZoom(20)

        mapController.setCenter(GeoPoint(latitud, longitud))  // Pintia

        // Define la lista de puntos para los marcadores
        val puntos = listOf(
            Punto("Las Quintana", 41.6239590929, -4.1734857708, R.drawable.ciudad, LasQuintanasActivity::class.java),
            Punto("Edificio UVa", 41.6130494436, -4.1640258634, R.drawable.uva, EdificioUVaActivity::class.java),
            Punto("Las Ruedas", latitud, longitud, R.drawable.cementerio, LasRuedasActivity::class.java),
            Punto("La Muralla", 41.6228752320, -4.1696152162, R.drawable.defensa, MurallaAsedioActivity::class.java),
            Punto("Las Ataque", 41.6222774251, -4.1682678963, R.drawable.ataque, MurallaAsedioActivity::class.java)
        )

        val leyenda = findViewById<Leyenda>(R.id.leyenda_main)
        val puntoMap = puntos.associateBy { it.title }
        leyenda.setMap(puntoMap)

        // Crea y configura los marcadores dinámicamente a partir de la lista de puntos
        for (punto in puntos) {
            val marker = Marker(mapView)
            marker.position = GeoPoint(punto.latitude, punto.longitude)
            marker.title = punto.title
            marker.icon = resources.getDrawable(punto.icon, null)

            // Configura el listener de clic para cada marcador
            marker.setOnMarkerClickListener { _, _ ->
                val intent = Intent(this, punto.destinationActivity)
                startActivity(intent)
                true

                // Mostrar mensaje Toast (opcional)
                Toast.makeText(this, "Marcador clickeado: ${punto.title}", Toast.LENGTH_SHORT).show()
                true // Indica que el evento ha sido manejado
            }

            // Agrega el marcador al mapa
            mapView.overlays.add(marker)
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
