package com.example.pintia

import API_TOKEN
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
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
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.pintia.puntosPrincipales.EdificioUVaFragment
import com.example.pintia.puntosPrincipales.LasQuintanasFragment
import com.example.pintia.puntosPrincipales.LasRuedasFragment
import com.example.pintia.puntosPrincipales.MurallaAsedioFragment
import com.example.pintia.services.DynamicViewBuilder.populateDynamicMarkers
import com.example.pintia.services.DynamicViewBuilder.saveMarkersToFile
import com.example.pintia.utils.ImageInfoWindow
import com.example.pintia.utils.TutorialManager
import com.example.pintia.utils.TutorialStep
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MapFragment : Fragment() {

    private lateinit var mapView: MapView

    //Coordenadas Ruedas
    private var latitud = 41.6169600023
    private var longitud = -4.1691941788
    private lateinit var locationManager: LocationManager
    private var userLocationMarker: Marker? = null  // Marcador de la ubicación en tiempo real
    private lateinit var centerLocationButton: LinearLayout

    private val CAMERA_REQUEST_CODE = 1
    private val CAMERA_PERMISSION_CODE = 1000

    private lateinit var tutorialOverlay: FrameLayout
    private lateinit var tutorialManager: TutorialManager

    private fun getMain(): MainActivity {
        return requireActivity() as MainActivity
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        val header = getMain().findViewById<Header>(R.id.header)
        header.title = getString(R.string.app_name)

        var camara: ImageButton = rootView.findViewById(R.id.btn_camera)
        camara.setOnClickListener {
            if (hasCameraPermission()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }

        // Inicializa la configuración de OSMDroid
        val context = requireContext()
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

        // Obtén una referencia al MapView
        mapView = rootView.findViewById(R.id.mapView)

        // Configura Mapbox como Tile Source
        mapView.setTileSource(mapboxTileSource)

        // Habilitar el zoom y el desplazamiento
        mapView.setMultiTouchControls(true)

        // Configura la cámara y la posición inicial
        val mapController: IMapController = mapView.controller
        mapController.setZoom(15.0)
        mapController.setCenter(GeoPoint(latitud, longitud))  // Pintia

        // Define la lista de puntos para los marcadores
        val puntos:List<Punto> = listOf(
            Punto(
                "Las Quintana",
                41.6239590929,
                -4.1734857708,
                R.drawable.ciudad,
                LasQuintanasFragment()
            ),
            Punto(
                "La Muralla",
                41.6228752320,
                -4.1696152162,
                R.drawable.defensa,
                MurallaAsedioFragment()
            ),
            Punto(
                "Las Ataque",
                41.6222774251,
                -4.1682678963,
                R.drawable.ataque,
                MurallaAsedioFragment()
            ),
            Punto(
                "Edificio UVa",
                41.6130494436,
                -4.1640258634,
                R.drawable.uva,
                EdificioUVaFragment()
            ),
            Punto(
                "Las Ruedas",
                latitud,
                longitud,
                R.drawable.cementerio,
                LasRuedasFragment()
            )
        )


        //coloca los markers de las imagenes
        val leyenda = rootView.findViewById<Leyenda>(R.id.leyenda_main)
        val puntoMap = puntos.associateBy { it.title }
        leyenda.setMap(puntoMap)


        // Crea y configura los marcadores dinámicamente a partir de la lista de puntos
        for (punto in puntos) {
            val marker = Marker(mapView)
            marker.position = GeoPoint(punto.latitude, punto.longitude)
            marker.title = punto.title
            marker.icon = punto.icon?.let { ResourcesCompat.getDrawable(resources, it, null) }

            // Configura el listener de clic para cada marcador
            marker.setOnMarkerClickListener { _, _ ->
                getMain().changeFragment(punto.fragment)
                Toast.makeText(context, "Marcador clickeado: ${punto.title}", Toast.LENGTH_SHORT)
                    .show()
                true
            }

            // Agrega el marcador al mapa
            mapView.overlays.add(marker)
        }

        var listMakers: List<Marker> = populateDynamicMarkers(context, mapView)
        for (marker in listMakers) {
            //println(marker.position.toString() +"|"+ marker.snippet)
            mapView.overlays.add(marker)
        }

        val buttonMap: LinearLayout = rootView.findViewById(R.id.btn_como_llegar)

        buttonMap.setOnClickListener {
            val latitude = 41.6130494436
            val longitude = -4.1640258634
            val label =
                getString(R.string.titulo_principal) // Puedes poner cualquier nombre de la ubicación

            // Crea el URI para abrir en Google Maps
            val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")

            // Crea un Intent con el URI
            val intent = Intent(Intent.ACTION_VIEW, uri)

            // Verifica si hay una aplicación capaz de manejar el Intent (Google Maps o navegador)
            intent.resolveActivity(context.packageManager)?.let {
                startActivity(intent)
            } ?: run {
                Toast.makeText(
                    context,
                    "No se encontró una aplicación para abrir Google Maps",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        tutorialOverlay = rootView.findViewById(R.id.tutorialOverlay)

        // Lista de pasos del tutorial
        val tutorialSteps = listOf(
            TutorialStep(
                R.drawable.defensa,
                getString(R.string.tut_marcador),
                getString(R.string.tut_marcador_desc)
            ),
            TutorialStep(
                R.drawable.tutorial_leyenda,
                getString(R.string.tut_Leyenda),
                getString(R.string.tut_leyenda_desc)
            ),
            TutorialStep(
                R.drawable.tutorial_my_ubi,
                getString(R.string.myUbi),
                getString(R.string.tut_my_ubi_desc)
            ),
            TutorialStep(
                R.drawable.tutorial_focus,
                getString(R.string.ubiPintia),
                getString(R.string.tut_focus_desc)
            ),
            TutorialStep(
                R.drawable.tutorial_como_llegar,
                getString(R.string.comoLlegar),
                getString(R.string.tut_como_llegar_desc)
            ),
            TutorialStep(
                R.drawable.tutorial_camara,
                getString(R.string.tut_camara),
                getString(R.string.tut_camara_desc)
            )
        )

        // Inicializar TutorialManager
        val activity = requireActivity()
        tutorialManager = TutorialManager(activity, tutorialOverlay, tutorialSteps)

        // Mostrar tutorial si es la primera vez
        if (TutorialManager.isFirstTimeTutorial(activity)) {
            tutorialManager.showTutorial()
        }

        // Configura LocationManager para obtener la ubicación en tiempo real
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Configura el botón para centrar en la ubicación
        centerLocationButton = rootView.findViewById<LinearLayout>(R.id.btn_center_location).apply {
            setOnClickListener {
                userLocationMarker?.position?.let { position ->
                    mapController.setCenter(position)
                }
            }
        }
        // Configura el botón para centrar en la ubicación
        val centerPintiaButton = rootView.findViewById<LinearLayout>(R.id.btn_center_pintia).apply {
            setOnClickListener {
                userLocationMarker?.position?.let { position ->
                    mapController.setCenter(GeoPoint(latitud, longitud))
                    mapController.setZoom(15)
                }
            }
        }

        // Intentamos obtener la última ubicación conocida si está disponible
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let { location ->
                updateLocationMarker(location)
                enableCenterButtonIfLocationAvailable() // Habilita el botón si hay una ubicación
            }
            startLocationUpdates() // Inicia actualizaciones de ubicación en tiempo real
        } else {
            // Si no hay permisos, solicitarlos
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

        return rootView
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
            return "$baseUrl$zoom/$x/$y.png?access_token=${API_TOKEN}"
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000,
            1f,
            locationListener
        )
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
                icon = ResourcesCompat.getDrawable(resources, R.drawable.user, null)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                mapView.overlays.add(this)
                Log.d("MapActivity", "Added: ${++i} locations")
            }
            Toast.makeText(context, "Ubicacion a tiempo real cargada", Toast.LENGTH_SHORT).show()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        } else {
            Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }

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
        if (::mapView.isInitialized) mapView.onDetach()  // Esto asegura que el mapa se destruya al salir de la actividad
        locationManager.removeUpdates(locationListener)  // Detiene las actualizaciones de ubicación
        mapView.overlayManager.clear()
    }

    private fun hasCameraPermission() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val photo: Bitmap? = data?.extras?.get("data") as? Bitmap
            photo?.let { savePhotoToCache(it) }
        }
    }

    private fun savePhotoToCache(bitmap: Bitmap) {
        val cacheDir = requireContext().cacheDir
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val fileName = "photo_$currentDate.jpg"
        val file = File(cacheDir, fileName)

        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            val location = Location("provider").apply {
                latitude = userLocationMarker?.position!!.latitude
                longitude = userLocationMarker?.position!!.longitude
            }
            addMarker(location, file.absolutePath, "Hola")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addMarker(location: Location, photoPath: String, desc: String) {
        val marker = Marker(mapView)
        marker.position = GeoPoint(location.latitude, location.longitude)
        marker.title = desc
        marker.icon = resources.getDrawable(R.drawable.point)
        marker.snippet = photoPath

        Log.d("PhotoPath", photoPath)

        // Crea y asigna la ventana de información personalizada
        val infoWindow = ImageInfoWindow(mapView)
        marker.infoWindow = infoWindow

        saveMarkersToFile(marker, requireContext())

        // Agrega el marcador al mapa
        mapView.overlays.add(marker)
    }

    // Método para verificar si es la primera vez que se muestra el tutorial
    private fun isFirstTimeTutorial(): Boolean {
        val preferences: SharedPreferences =
            requireContext().getSharedPreferences("TutorialPreferences", Context.MODE_PRIVATE)
        return preferences.getBoolean("TutorialShown", true) // Por defecto, true (primera vez)
    }
}
