package com.example.pintia.puntosPrincipales.lasQuintanasViews

import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.R
import com.example.pintia.components.Header
import com.example.pintia.services.DynamicViewBuilder.loadContentFromJson
import com.example.pintia.services.DynamicViewBuilder.populateDynamicDescription
import com.example.pintia.services.TTSManager

class YacimientoInfoView : AppCompatActivity() {

    private lateinit var ttsManager: TTSManager
    private var idOfAudioPlaying = -1

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

        val audioView = findViewById<ImageButton>(R.id.audio_player).apply {
            id = View.generateViewId()
            setOnClickListener {
                // Si un botón reproduciendo y es el mismo botón -> parar
                // Si otro botón esta activo -> parar y reproducir
                // Si no está reproduciendo -> reproducir
                val oldId = idOfAudioPlaying
                if (idOfAudioPlaying != -1 && ttsManager.getIsPlaying()) { // Reproduciendo
                    ttsManager.stop()
                    findViewById<ImageButton>(idOfAudioPlaying).setBackgroundResource(R.drawable.round_button_background)
                    idOfAudioPlaying = -1
                }
                if ((oldId == -1 || oldId != id) && tituloTTL.isNotEmpty()) { // Si no hay otro reproduciendo o si otro botón estaba activado
                    speakText(tituloTTL)
                    idOfAudioPlaying = id
                    it.setBackgroundResource(R.drawable.round_button_selected_background)
                }
                // En caso de ser el mismo botón para (se hace con el primer if y sin acceder al segundo)
            }
        }

        val audioView_2 = findViewById<ImageButton>(R.id.audio_player_2).apply {
            id = View.generateViewId()
            setOnClickListener {
                // Si un botón reproduciendo y es el mismo botón -> parar
                // Si otro botón esta activo -> parar y reproducir
                // Si no está reproduciendo -> reproducir
                val oldId = idOfAudioPlaying
                if (idOfAudioPlaying != -1 && ttsManager.getIsPlaying()) { // Reproduciendo
                    ttsManager.stop()
                    findViewById<ImageButton>(idOfAudioPlaying).setBackgroundResource(R.drawable.round_button_background)
                    idOfAudioPlaying = -1
                }
                if ((oldId == -1 || oldId != id) && tituloTTL.isNotEmpty()) { // Si no hay otro reproduciendo o si otro botón estaba activado
                    speakText(moreTTL)
                    idOfAudioPlaying = id
                    it.setBackgroundResource(R.drawable.round_button_selected_background)
                }
                // En caso de ser el mismo botón para (se hace con el primer if y sin acceder al segundo)
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



    // Función para leer el texto
    private fun speakText(text: String) {
        ttsManager.speak(text, "InfoActivityUtterance")
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