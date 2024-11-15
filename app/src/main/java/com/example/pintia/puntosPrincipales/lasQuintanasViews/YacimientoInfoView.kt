package com.example.pintia.puntosPrincipales.lasQuintanasViews

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.R
import com.example.pintia.components.Header
import com.example.pintia.services.DynamicViewBuilder.loadContentFromJson
import com.example.pintia.services.DynamicViewBuilder.populateDynamicDescription

class YacimientoInfoView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.component_info_views)
        val header = findViewById<Header>(R.id.header)
        // Recuperar los datos del Intent
        val titulo = intent.getStringExtra("title").toString()
        header.title = titulo

//        val contentItems = listOf(
//            ContentItem("text", "Esta es la primera descripción."),
//            ContentItem("image", "https://http.cat/404.jpg"), // URL de una imagen
//            ContentItem("text", "Otra sección de descripción."),
//            ContentItem("image", "https://http.cat/404.jpg") // Otra imagen
//        )

        // Carga los datos desde el JSON
        val titulo_cod = titulo.lowercase().replace(" ", "_")
        println(titulo_cod)
        val contentItems = loadContentFromJson(this, "data_${titulo_cod}.json")
        Log.d("JSONView", contentItems.toString())

        val dynamicContainer = findViewById<LinearLayout>(R.id.dynamic_description_container)
        populateDynamicDescription(dynamicContainer, contentItems)
    }
}