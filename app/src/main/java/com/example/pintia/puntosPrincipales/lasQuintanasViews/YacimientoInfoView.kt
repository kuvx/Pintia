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
        val path = intent.getStringExtra("path").toString()
        header.title = titulo

        // Carga los datos desde el JSON
        println(path)
        var titulo_cod =""
        when(path){
            "ruedas" ->{
                titulo_cod= titulo.split(".")[0]
            }
            "quintana" ->{
               titulo_cod = titulo.lowercase().replace(" ", "_")
            }

        }
        val contentItems = loadContentFromJson(this, "${path}/data_${titulo_cod}.json", true)
        Log.d("JSONView", contentItems.toString())

        val dynamicContainer = findViewById<LinearLayout>(R.id.dynamic_description_container)
        populateDynamicDescription(dynamicContainer, contentItems)

        val contentItems_moreInfo = loadContentFromJson(this, "${path}/data_${titulo_cod}_more.json", true)
        Log.d("JSONView", contentItems.toString())

        val dynamicContainer_more = findViewById<LinearLayout>(R.id.dynamic_more_info_container)
        populateDynamicDescription(dynamicContainer_more, contentItems_moreInfo)

    }
}