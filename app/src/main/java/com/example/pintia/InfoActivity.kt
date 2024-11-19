package com.example.pintia

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.components.Header
import com.example.pintia.services.DynamicViewBuilder.loadContentFromJson
import com.example.pintia.services.DynamicViewBuilder.populateDynamicDescription
import com.example.pintia.services.TTSManager
import java.util.Locale

class InfoActivity : AppCompatActivity(){

    private lateinit var ttsManager: TTSManager
    private var idOfAudioPlaying = -1

    private fun addTitle(title: String): String {
        return "$title\n\n"
    }

    private fun addParagraph(paragraph: String): String {
        return "$paragraph\n"
    }

    val data = listOf<Pair<Int, Int>>(
        Pair(R.string.info_description_title, R.string.info_description_content),
        Pair(R.string.info_more_info_title, R.string.info_more_info_content),
        Pair(R.string.info_challenges_title, R.string.info_challenges_content),
    )

    private fun addEntry(layout: LinearLayout, value: Pair<Int, Int>){
        val entry = LayoutInflater.from(this).inflate(
            R.layout.activity_info_entry_component,
            layout,
            false
        ) as LinearLayout
        layout.addView(entry)
        val (title, content) = value

        var textToInsert = getString(title)
        var textTS = addTitle(textToInsert)

        val titleView = findViewById<TextView>(R.id.info_comp_title)
        titleView.id = View.generateViewId()
        titleView.text = textToInsert

        textToInsert = getString(content)
        textTS = "$textTS\n $textToInsert\n"

        val contentView = findViewById<TextView>(R.id.info_comp_content)
        contentView.id = View.generateViewId()
        contentView.text = textToInsert

        val audioView = findViewById<ImageButton>(R.id.audio_player)
        audioView.id = View.generateViewId()
        audioView.id = View.generateViewId()

        audioView.setOnClickListener {
            // Si un botón reproduciendo y es el mismo botón -> parar
            // Si otro botón esta activo -> parar y reproducir
            // Si no está reproduciendo -> reproducir
            val oldId = idOfAudioPlaying
            if (idOfAudioPlaying != -1 && ttsManager.getIsPlaying()) { // Reproduciendo
                ttsManager.stop()
                findViewById<ImageButton>(idOfAudioPlaying).setBackgroundResource(R.drawable.round_button_background)
                idOfAudioPlaying = -1
            }
            if ((oldId == -1 || oldId != audioView.id) && textTS.isNotEmpty()) { // Si no hay otro reproduciendo o si otro botón estaba activado
                speakText(textTS)
                idOfAudioPlaying = audioView.id
                audioView.setBackgroundResource(R.drawable.round_button_selected_background)
            }
            // En caso de ser el mismo botón para (se hace con el primer if y sin acceder al segundo)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.info)

        val layout = findViewById<LinearLayout>(R.id.info_main_content)
        layout.removeAllViews()

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

        var path = "aboutUs"
        var titulo_cod = getString(R.string.info).lowercase().replace(" ", "_")
        val contentItems = loadContentFromJson(this, "${path}/data_${titulo_cod}.json", true)
        Log.d("JSONView", contentItems.toString())

        val dynamicContainer = findViewById<LinearLayout>(R.id.dynamic_description_container)
        populateDynamicDescription(dynamicContainer, contentItems)

        val contentItems_moreInfo = loadContentFromJson(this, "${path}/data_${titulo_cod}_more.json", true)
        Log.d("JSONView", contentItems.toString())

        val dynamicContainer_more = findViewById<LinearLayout>(R.id.dynamic_more_info_container)
        populateDynamicDescription(dynamicContainer_more, contentItems_moreInfo)


        val requestButton = findViewById<Button>(R.id.visit_request_button)
        requestButton.setOnClickListener {
            val intent = Intent(this, RequestVisitActivity::class.java)
            startActivity(intent)
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