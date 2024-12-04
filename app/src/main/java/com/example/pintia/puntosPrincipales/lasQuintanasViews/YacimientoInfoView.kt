package com.example.pintia.puntosPrincipales.lasQuintanasViews

import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
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
import com.example.pintia.utils.TutorialManager
import com.example.pintia.utils.TutorialStep
import java.text.Normalizer

class YacimientoInfoView : AppCompatActivity() {

    private lateinit var ttsManager: TTSManager
    private var idOfAudioPlaying = -1

    private lateinit var tutorialOverlay: FrameLayout
    private lateinit var tutorialManager: TutorialManager


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

        tutorialOverlay = findViewById(R.id.tutorialOverlay)

        // Lista de pasos del tutorial
        val tutorialSteps = listOf(
            TutorialStep(R.drawable.tutorial_audio, getString(R.string.tut_audio), getString(R.string.tut_audio_desc))
        )

        // Inicializar TutorialManager
        tutorialManager = TutorialManager(this, tutorialOverlay, tutorialSteps)

        // Mostrar tutorial si es la primera vez
        if (TutorialManager.isFirstTimeTutorial(this)) {
            tutorialManager.showTutorial()
        }

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