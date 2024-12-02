package com.example.pintia.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.pintia.R
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

class ImageInfoWindow(mapView: MapView) : InfoWindow(R.layout.marker_info_window, mapView) {
    override fun onOpen(item: Any?) {
        val marker = item as Marker
        val view = mView.findViewById<ImageView>(R.id.info_window_image)
        Glide.with(mapView.context)
            .load(marker.snippet)
            .into(view)
        view.setOnClickListener {
            close() // Cierra el InfoWindow
        }
    }

    override fun onClose() {
        // No es necesario implementar nada aquí
    }
}