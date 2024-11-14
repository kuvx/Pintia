package com.example.pintia.services

import com.example.pintia.models.PoligonoColor

interface OnPoligonoTouchListener {
    fun onPoligonoTouched(poligono: PoligonoColor)
}