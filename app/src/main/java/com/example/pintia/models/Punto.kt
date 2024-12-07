package com.example.pintia.models

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

data class Punto(
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val icon: Int?, // Referencia de recurso para el icono (por ejemplo, R.drawable.ic_point_marker)
    val fragment: Fragment // Fragmento al que redirigir
)