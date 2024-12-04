package com.example.pintia

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.components.Header
import com.example.pintia.services.DynamicViewBuilder.pueblaActivity
import com.example.pintia.services.TTSManager
import java.text.Normalizer

class InfoActivity : AppCompatActivity(){

    private lateinit var ttsManager: TTSManager
    private var idOfAudioPlaying = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component_info_views)

        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.info)

        var layout: RelativeLayout = findViewById(R.id.component_info_views)
        ttsManager = TTSManager(this) { success ->
            if (success) {
                // TTS inicializado correctamente
                ttsManager.setupListener(this, idOfAudioPlaying)
            } else {
                // Manejar el error de inicialización
            }
        }


        var path = "aboutUs"
        var titulo_cod = getString(R.string.info).lowercase().replace(" ", "_")
        titulo_cod = Normalizer.normalize(titulo_cod, Normalizer.Form.NFD).replace(Regex("\\p{M}"), "")

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