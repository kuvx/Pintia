package com.example.pintia.puntosPrincipales

import android.content.Intent
import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.R
import com.example.pintia.RequestVisitActivity
import com.example.pintia.components.Header
import com.example.pintia.services.DynamicViewBuilder.loadContentFromJson
import com.example.pintia.services.DynamicViewBuilder.populateDynamicDescription
import com.example.pintia.services.TTSManager

class MurallaAsedioActivity : AppCompatActivity() {
    private lateinit var ttsManager: TTSManager
    private var idOfAudioPlaying = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_muralla_asedio)

        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.muralla)

        ttsManager = TTSManager(this) { success ->
            if (success) {
                // TTS inicializado correctamente
                setupTTSListener()
            } else {
                // Manejar el error de inicialización
            }
        }

//        data.forEach {
//            addEntry(layout, it)
//        }

        var path = "muralla"
        var titulo_cod = getString(R.string.info).lowercase().replace(" ", "_")

        val contentItems = loadContentFromJson(this, "${path}/data_${titulo_cod}.json", true)
        Log.d("JSONView", contentItems.toString())

        val dynamicContainer = findViewById<LinearLayout>(R.id.dynamic_description_container)
        val tituloTTL = populateDynamicDescription(getString(R.string.description),dynamicContainer, contentItems)

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

