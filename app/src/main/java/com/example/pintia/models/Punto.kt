package com.example.pintia.models

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity

data class Punto(
    val title : String,
    val latitude: Double,
    val longitude: Double,
    val icon: Int, // Referencia de recurso para el icono (por ejemplo, R.drawable.ic_point_marker)
    val destinationActivity: Class<out AppCompatActivity> // Clase de la actividad a redirigir
)