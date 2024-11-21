package com.example.pintia
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.osmdroid.views.overlay.Polyline
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.pintia.components.Header
import com.example.pintia.components.Leyenda
import com.example.pintia.models.Punto
import com.example.pintia.puntosPrincipales.EdificioUVaActivity
import com.example.pintia.puntosPrincipales.LasQuintanasActivity
import com.example.pintia.puntosPrincipales.LasRuedasActivity
import com.example.pintia.puntosPrincipales.MurallaAsedioActivity
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*
import android.Manifest
import android.util.Log
import android.widget.ImageButton
import java.io.IOException


class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private val API_token = "pk.eyJ1IjoicGludGlhcHJveWVjdDI0IiwiYSI6ImNtMzdqNnNlaTA5emIybHF1NGU2OXI3Y2MifQ.4VtNOpGHNw88xqol5bl7pA"
    //Coordenadas Ruedas
        private var latitud=41.6169600023
    private var longitud=-4.1691941788
    private lateinit var locationManager: LocationManager
    private var userLocationMarker: Marker? = null  // Marcador de la ubicación en tiempo real
    private lateinit var centerLocationButton :ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.app_name)

        // Inicializa la configuración de OSMDroid
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))

        // Obtén una referencia al MapView
        mapView = findViewById(R.id.mapView)

        // Configura Mapbox como Tile Source
        mapView.setTileSource(mapboxTileSource)

        // Habilitar el zoom y el desplazamiento
        mapView.setMultiTouchControls(true)

        // Configura la cámara y la posición inicial
        val mapController: IMapController = mapView.controller
        mapController.setZoom(15)
        mapController.setCenter(GeoPoint(latitud, longitud))  // Pintia

        // Define la lista de puntos para los marcadores
        val puntos = listOf(
            Punto("Las Quintana", 41.6239590929, -4.1734857708, R.drawable.ciudad, LasQuintanasActivity::class.java),
            Punto("La Muralla", 41.6228752320, -4.1696152162, R.drawable.defensa, MurallaAsedioActivity::class.java),
            Punto("Las Ataque", 41.6222774251, -4.1682678963, R.drawable.ataque, MurallaAsedioActivity::class.java),
            Punto("Edificio UVa", 41.6130494436, -4.1640258634, R.drawable.uva, EdificioUVaActivity::class.java),
            Punto("Las Ruedas", latitud, longitud, R.drawable.cementerio, LasRuedasActivity::class.java)
        )

        val leyenda = findViewById<Leyenda>(R.id.leyenda_main)
        val puntoMap = puntos.associateBy { it.title }
        leyenda.setMap(puntoMap)

//        // Crear la Polyline de la ruta
//        val geoPoints = puntos.map { GeoPoint(it.latitude, it.longitude) }
//        val rutaPolyline = Polyline()
//        rutaPolyline.setPoints(geoPoints)
//        rutaPolyline.color = resources.getColor(R.color.black, null) // Color de la línea
//
//        // Agrega la Polyline (ruta) al mapa
//        mapView.overlays.add(rutaPolyline)
        fetchRouteFromMapbox(GeoPoint(puntos[4].latitude,puntos[4].longitude),GeoPoint(puntos[3].latitude,puntos[3].longitude))
        fetchRouteFromMapbox(GeoPoint(41.6228752320,-4.1696152162),GeoPoint(puntos[4].latitude,puntos[4].longitude))


        // Crea y configura los marcadores dinámicamente a partir de la lista de puntos
        for (punto in puntos) {
            val marker = Marker(mapView)
            marker.position = GeoPoint(punto.latitude, punto.longitude)
            marker.title = punto.title
            marker.icon = punto.icon?.let { resources.getDrawable(it, null) }

            // Configura el listener de clic para cada marcador
            marker.setOnMarkerClickListener { _, _ ->
                val intent = Intent(this, punto.destinationActivity)
                startActivity(intent)
                Toast.makeText(this, "Marcador clickeado: ${punto.title}", Toast.LENGTH_SHORT).show()
                true
            }

            // Agrega el marcador al mapa
            mapView.overlays.add(marker)
        }



        // Configura LocationManager para obtener la ubicación en tiempo real
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Configura el botón para centrar en la ubicación
        centerLocationButton = findViewById<ImageButton>(R.id.btn_center_location).apply {
            isEnabled = false // Deshabilitado inicialmente
            setOnClickListener {
                userLocationMarker?.position?.let { position ->
                    mapController.setCenter(position)
                }
            }
        }
        // Configura el botón para centrar en la ubicación
        val centerPintiaButton = findViewById<ImageButton>(R.id.btn_center_pintia).apply {
            setOnClickListener {
                userLocationMarker?.position?.let { position ->
                    mapController.setCenter(GeoPoint(latitud, longitud))
                }
            }
        }

        // Intentamos obtener la última ubicación conocida si está disponible
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let { location ->
                updateLocationMarker(location)
                enableCenterButtonIfLocationAvailable() // Habilita el botón si hay una ubicación
            }
            startLocationUpdates() // Inicia actualizaciones de ubicación en tiempo real
        } else {
            // Si no hay permisos, solicitarlos
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    // Define la fuente de tiles personalizados para Mapbox
    private val mapboxTileSource = object : OnlineTileSourceBase(
        "Mapbox", // Nombre del tile source
        1, 19, 512, ".png", // Zoom mínimo, máximo, tamaño de tile, extensión
        arrayOf("https://api.mapbox.com/v4/mapbox.satellite/")
    ) {
        override fun getTileURLString(pMapTileIndex: Long): String {
            val zoom = MapTileIndex.getZoom(pMapTileIndex)
            val x = MapTileIndex.getX(pMapTileIndex)
            val y = MapTileIndex.getY(pMapTileIndex)
            return "$baseUrl$zoom/$x/$y.png?access_token=${API_token}"
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, locationListener)
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            updateLocationMarker(location)
            enableCenterButtonIfLocationAvailable() // Habilita el botón cuando llega la ubicación en tiempo real
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    var i = 0

    private fun updateLocationMarker(location: Location) {
        val userLocation = GeoPoint(location.latitude, location.longitude)
        if (userLocationMarker == null) {
            // Crear el marcador de ubicación del usuario si no existe
            userLocationMarker = Marker(mapView).apply {
                icon = resources.getDrawable(R.drawable.user, null)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                mapView.overlays.add(this)
                Log.d("MapActivity","Added: ${++i} locations");
            }
        }
        // Actualizar la posición del marcador y refrescar el mapa
        userLocationMarker?.position = userLocation
        //mapView.invalidate()
    }

    private fun enableCenterButtonIfLocationAvailable() {
        // Habilita el botón solo si la ubicación es válida y el marcador está configurado
        if (userLocationMarker != null) {
            centerLocationButton.isEnabled = true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
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
        locationManager.removeUpdates(locationListener)  // Detiene las actualizaciones de ubicación
        mapView.overlayManager.clear()
    }
    fun fetchRouteFromMapbox(origin: GeoPoint, destination: GeoPoint) {
        val client = OkHttpClient()

        // URL de Mapbox Directions API (reemplaza ACCESS_TOKEN con tu token de acceso)
        val url = "https://api.mapbox.com/directions/v5/mapbox/driving/${origin.longitude},${origin.latitude};${destination.longitude},${destination.latitude}?geometries=geojson&access_token=${API_token}"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use { responseBody ->  // Usar .use para cerrar automáticamente el cuerpo
                    if (!responseBody.isSuccessful) throw IOException("Unexpected code $responseBody")

                    val jsonResponse = JSONObject(responseBody.body()!!.string())
                    val routes = jsonResponse.getJSONArray("routes")
                    if (routes.length() > 0) {
                        val route = routes.getJSONObject(0)
                        val geometry = route.getJSONObject("geometry")
                        val coordinates = geometry.getJSONArray("coordinates")

                        // Lista de puntos para la Polyline
                        val geoPoints = mutableListOf<GeoPoint>()
                        for (i in 0 until coordinates.length()) {
                            val point = coordinates.getJSONArray(i)
                            val lon = point.getDouble(0)
                            val lat = point.getDouble(1)
                            geoPoints.add(GeoPoint(lat, lon))
                        }

                        // Dibujar la Polyline en el mapa
                        runOnUiThread {
                            val routeLine = Polyline().apply {
                                setPoints(geoPoints)
                                color = resources.getColor(R.color.black, null)
                            }
                            mapView.overlays.add(routeLine)
                            mapView.invalidate() // Refresca el mapa para mostrar la ruta
                        }
                    }
                }
            }
        })
    }
}
