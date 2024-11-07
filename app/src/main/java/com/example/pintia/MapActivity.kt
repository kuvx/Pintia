package com.example.pintia

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pintia.components.Header
import com.example.pintia.models.Punto
import com.google.android.gms.maps.model.Marker
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    //Coordenadas Ruedas
    private var latitud=41.6169600023
    private var longitud=-4.1691941788


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.app_name)

        // Inicializa la configuración de OSMDroid
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))

        // Obtén una referencia al MapView
        mapView = findViewById(R.id.mapView)

        // Configura la fuente de tiles (mapa)
        mapView.setTileSource(TileSourceFactory.MAPNIK)  // Puedes usar diferentes fuentes de tiles

        // Habilitar el zoom y el desplazamiento
        mapView.setMultiTouchControls(true)  // Permite hacer zoom y mover el mapa con gestos

        // Configura la cámara y la posición inicial
        val mapController: IMapController = mapView.controller
        mapController.setZoom(16.8)
        mapController.setCenter(GeoPoint(latitud, longitud))  // Pintia

        // Define la lista de puntos para los marcadores
        val puntos = listOf(
            Punto("Las Quintana", 41.6239590929, -4.1734857708, R.drawable.ciudad, GalleryActivity::class.java),
            Punto("Edificio UVa", 41.6130494436, -4.1640258634, R.drawable.uva, SettingsActivity::class.java),
            Punto("Las Ruedas", latitud, longitud, R.drawable.cementerio, RequestVisitActivity::class.java),
            Punto("La Muralla", latitud, longitud, R.drawable.defensa, RequestVisitActivity::class.java),
            Punto("Las Ataque", latitud, longitud, R.drawable.ataque, RequestVisitActivity::class.java)
        )

        // Crea y configura los marcadores dinámicamente a partir de la lista de puntos
        for (punto in puntos) {
            val marker = Marker(mapView)
            marker.position = GeoPoint(punto.latitude, punto.longitude)
            marker.title = punto.title
            marker.icon = resources.getDrawable(punto.icon, null)

            // Configura el listener de clic para cada marcador
            marker.setOnMarkerClickListener { _, _ ->
                val intent = Intent(this, GalleryActivity::class.java)
                startActivity(intent)

                // Mostrar mensaje Toast (opcional)
                Toast.makeText(this, "Marcador clickeado: ${punto.title}", Toast.LENGTH_SHORT).show()
                true // Indica que el evento ha sido manejado
            }

            // Agrega el marcador al mapa
            mapView.overlays.add(marker)
        }
    }
}