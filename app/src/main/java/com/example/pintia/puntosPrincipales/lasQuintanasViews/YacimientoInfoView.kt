package com.example.pintia.puntosPrincipales.lasQuintanasViews

import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.R
import com.example.pintia.components.Header
import com.example.pintia.services.DynamicViewBuilder.loadContentFromJson
import com.example.pintia.services.DynamicViewBuilder.populateDynamicDescription
import com.example.pintia.services.TTSManager
import com.example.pintia.services.AudioButtonHandler
import com.example.pintia.services.DynamicViewBuilder.pueblaActivity
import java.text.Normalizer

class YacimientoInfoView : AppCompatActivity() {

    private lateinit var ttsManager: TTSManager
    private var idOfAudioPlaying = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component_info_views)
        val header = findViewById<Header>(R.id.header)
        // Recuperar los datos del Intent
        val titulo = intent.getStringExtra("title").toString()
        val path = intent.getStringExtra("path").toString()
        header.title = titulo

        var layout: RelativeLayout = findViewById(R.id.component_info_views)
        ttsManager = TTSManager(this) { success ->
            if (success) {
                // TTS inicializado correctamente
                ttsManager.setupListener(this, idOfAudioPlaying)
            } else {
                // Manejar el error de inicialización
            }
        }

        // Carga los datos desde el JSON
        println(path)
        var titulo_cod =""
        when(path){
            "ruedas" ->{
                titulo_cod= titulo.split(".")[0]
            }
            "quintana" ->{
                titulo_cod = titulo.lowercase().replace(" ", "_")
                titulo_cod = Normalizer.normalize(titulo_cod, Normalizer.Form.NFD).replace(Regex("\\p{M}"), "")

            }

        }
        pueblaActivity(layout,this, path, titulo_cod, ttsManager)

    }

    override fun onPause() {
        ttsManager.stop()
        super.onPause()
    }

    override fun onDestroy() {
        ttsManager.shutdown()
        super.onDestroy()
    }
}