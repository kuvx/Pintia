package com.example.pintia

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pintia.components.Header
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

        // Configura un área de latitudes y longitudes específicas
        val quintana = GeoPoint(41.6239590929, -4.1734857708)  // Coordenada Quinanas
        val uva = GeoPoint(41.6130494436, -4.1640258634)  // Coordenada Edificio UVa
        val ruedas = GeoPoint(latitud, longitud)  // Coordenada Edificio UVa

        // Añadir marcadores en las coordenadas especificadas
        val marker1 = Marker(mapView)
        marker1.position = quintana
        marker1.title = "Las Quintana"
        marker1.icon = resources.getDrawable(R.drawable.ciudad)
        mapView.overlays.add(marker1)

        val marker2 = Marker(mapView)
        marker2.position = uva
        marker2.title = "Edificio UVa"
        marker2.icon = resources.getDrawable(R.drawable.uva)

        mapView.overlays.add(marker2)

        val marker3 = Marker(mapView)
        marker3.position = ruedas
        marker3.title = "Las Ruedas"
        marker3.icon = resources.getDrawable(R.drawable.cementerio)
        mapView.overlays.add(marker3)
    }
}