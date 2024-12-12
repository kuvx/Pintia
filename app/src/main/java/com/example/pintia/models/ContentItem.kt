package com.example.pintia.models

import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.R

data class ContentItem(
    val type: String,
    val value: String,
    val latitude: Double,
    val longitude: Double,
    val icon: Int = R.drawable.point,
    val action: Class<out AppCompatActivity>?
)
