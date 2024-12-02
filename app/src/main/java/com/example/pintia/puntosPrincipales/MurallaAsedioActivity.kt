package com.example.pintia.puntosPrincipales

import android.content.Intent
import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.R
import com.example.pintia.R.*
import com.example.pintia.RequestVisitActivity
import com.example.pintia.components.Header
import com.example.pintia.services.AudioButtonHandler
import com.example.pintia.services.DynamicViewBuilder.loadContentFromJson
import com.example.pintia.services.DynamicViewBuilder.populateDynamicDescription
import com.example.pintia.services.DynamicViewBuilder.pueblaActivity
import com.example.pintia.services.TTSManager

class MurallaAsedioActivity : AppCompatActivity() {
    private lateinit var ttsManager: TTSManager
    private var idOfAudioPlaying = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.component_info_views)

        val header = findViewById<Header>(id.header)
        header.title = getString(string.muralla)

        var layout: RelativeLayout = findViewById(R.id.component_info_views)
        //cambiamos el fondo
        layout.setBackgroundResource(R.drawable.fondo_defensa)
        ttsManager = TTSManager(this) { success ->
            if (success) {
                // TTS inicializado correctamente
                ttsManager.setupListener(this, idOfAudioPlaying)
            } else {
                // Manejar el error de inicialización
            }
        }
        

        var path = "muralla"
        var titulo_cod = getString(string.muralla).lowercase().replace(" ", "_")

        pueblaActivity(layout,this, path, titulo_cod,ttsManager)


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

