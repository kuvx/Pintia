package com.example.pintia.puntosPrincipales

import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.R
import com.example.pintia.components.Header
import com.example.pintia.services.DynamicViewBuilder.pueblaActivity
import com.example.pintia.services.TTSManager

class MurallaAsedioActivity : AppCompatActivity() {
    private lateinit var ttsManager: TTSManager
    private var idOfAudioPlaying = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component_info_views)

        val header = findViewById<Header>(R.id.header)
        val text = getString(R.string.muralla)
        header.title = text

        val layout: RelativeLayout = findViewById(R.id.component_info_views)
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
        

        val path = "muralla"
        val tituloCod = text.lowercase().replace(" ", "_")

        pueblaActivity(layout,this, path, tituloCod,ttsManager)


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

