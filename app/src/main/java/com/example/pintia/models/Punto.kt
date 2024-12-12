package com.example.pintia.models

import androidx.fragment.app.Fragment

data class Punto(
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val icon: Int?, // ID del icono
    val fragment: Fragment // Fragmento al que redirigir
)