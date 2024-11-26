package com.example.pintia.puntosPrincipales.lasQuintanasViews

import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.R
import com.example.pintia.components.Header
import com.example.pintia.services.DynamicViewBuilder.loadContentFromJson
import com.example.pintia.services.DynamicViewBuilder.populateDynamicDescription
import com.example.pintia.services.TTSManager
import com.example.pintia.services.AudioButtonHandler
import java.text.Normalizer

class YacimientoInfoView : AppCompatActivity() {

    private lateinit var ttsManager: TTSManager
    private var idOfAudioPlaying = -1
    private lateinit var audioButtonHandler: AudioButtonHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.component_info_views)
        val header = findViewById<Header>(R.id.header)
        // Recuperar los datos del Intent
        val titulo = intent.getStringExtra("title").toString()
        val path = intent.getStringExtra("path").toString()
        header.title = titulo

        ttsManager = TTSManager(this) { success ->
            if (success) {
                // TTS inicializado correctamente
                setupTTSListener()
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
        val contentItems = loadContentFromJson(this, "${path}/data_${titulo_cod}.json", true)
        Log.d("JSONView", contentItems.toString())

        val dynamicContainer = findViewById<LinearLayout>(R.id.dynamic_description_container)
        val tituloTTL =populateDynamicDescription(getString(R.string.description),dynamicContainer, contentItems)

        val contentItems_moreInfo = loadContentFromJson(this, "${path}/data_${titulo_cod}_more.json", true)
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