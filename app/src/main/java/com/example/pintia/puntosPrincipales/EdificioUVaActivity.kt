package com.example.pintia.puntosPrincipales

import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.R
import com.example.pintia.components.Header
import com.example.pintia.services.AudioButtonHandler
import com.example.pintia.services.DynamicViewBuilder.loadContentFromJson
import com.example.pintia.services.DynamicViewBuilder.populateDynamicDescription
import com.example.pintia.services.DynamicViewBuilder.pueblaActivity
import com.example.pintia.services.TTSManager


class EdificioUVaActivity : AppCompatActivity() {

    private lateinit var ttsManager: TTSManager
    private var idOfAudioPlaying = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component_info_views)

        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.titulo_principal)

        var layout: RelativeLayout = findViewById(R.id.component_info_views)
        ttsManager = TTSManager(this) { success ->
            if (success) {
                // TTS inicializado correctamente
                ttsManager.setupListener(this, idOfAudioPlaying)
            } else {
                // Manejar el error de inicialización
            }
        }


        var path = "edificio"
        pueblaActivity(layout,this, path, path,ttsManager)

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