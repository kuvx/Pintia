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
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.R
import com.example.pintia.components.Header
import com.example.pintia.services.AudioButtonHandler
import com.example.pintia.services.DynamicViewBuilder.loadContentFromJson
import com.example.pintia.services.DynamicViewBuilder.populateDynamicDescription
import com.example.pintia.services.TTSManager


class EdificioUVaActivity : AppCompatActivity() {

    private lateinit var ttsManager: TTSManager
    private var idOfAudioPlaying = -1
    private lateinit var audioButtonHandler: AudioButtonHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edificio_uva)

        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.titulo_principal)

        ttsManager = TTSManager(this) { success ->
            if (success) {
                // TTS inicializado correctamente
                setupTTSListener()
            } else {
                // Manejar el error de inicialización
            }
        }


        var path = "edificio"

        val contentItems = loadContentFromJson(this, "${path}/data_${path}.json", true)
        Log.d("JSONView", contentItems.toString())

        val dynamicContainer = findViewById<LinearLayout>(R.id.dynamic_description_container)
        val tituloTTL = populateDynamicDescription(getString(R.string.description),dynamicContainer, contentItems)

        val contentItems_moreInfo = loadContentFromJson(this, "${path}/data_${path}_more.json", true)
        Log.d("JSONView", contentItems.toString())

        val dynamicContainer_more = findViewById<LinearLayout>(R.id.dynamic_more_info_container)
        val moreTTL = populateDynamicDescription(getString(R.string.more_info_title), dynamicContainer_more, contentItems_moreInfo)

        // Inicializar el manejador de botones de audio
        audioButtonHandler = AudioButtonHandler(this, ttsManager)

        // Configurar botones de audio
        val audioView1 = findViewById<ImageButton>(R.id.audio_player)
        val audioView2 = findViewById<ImageButton>(R.id.audio_player_2)

        // Configurar cada botón con su respectivo texto
        audioButtonHandler.setupAudioButton(audioView1, tituloTTL)
        audioButtonHandler.setupAudioButton(audioView2, moreTTL)

        // Configuración del Spinner_1
        val speedSelector: Spinner = findViewById(R.id.speed_selector)
        speedSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val speeds =
                    arrayOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f) // Velocidades disponibles
                val selectedSpeed = speeds[position]

                // Cambiar la velocidad del TTS a través de TTSManager
                ttsManager.setSpeechRate(selectedSpeed)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No se seleccionó nada
            }
        }

        // Configuración del Spinner_2
        val speedSelector_2: Spinner = findViewById(R.id.speed_selector_2)
        speedSelector_2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val speeds =
                    arrayOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f) // Velocidades disponibles
                val selectedSpeed = speeds[position]

                // Cambiar la velocidad del TTS a través de TTSManager
                ttsManager.setSpeechRate(selectedSpeed)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No se seleccionó nada
            }
        }
    }

    private fun setupTTSListener() {
        ttsManager.setUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                println("Iniciando lectura...")
            }

            override fun onDone(utteranceId: String?) {
                runOnUiThread {
                    println("Lectura finalizada.")
                    findViewById<ImageButton>(idOfAudioPlaying).setBackgroundResource(R.drawable.round_button_background)
                    idOfAudioPlaying = -1
                }
            }

            override fun onError(utteranceId: String?) {
                println("Error al leer el texto.")
            }
        })
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