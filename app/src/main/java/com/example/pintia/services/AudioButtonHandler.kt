package com.example.pintia.services

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Spinner
import com.example.pintia.R
import com.example.pintia.services.TTSManager

class AudioButtonHandler(
    private val context: Context,
    private val ttsManager: TTSManager
) {

    // Variable para rastrear qué botón está reproduciendo
    private var idOfAudioPlaying: Int = -1

    /**
     * Configura un botón para manejar la reproducción de audio.
     * @param button El botón (ImageButton) que se usará para reproducir audio.
     * @param text El texto que debe ser leído en voz alta al presionar el botón.
     */
    fun setupAudioButton(button: ImageButton, text: String) {
        button.id = View.generateViewId() // Genera un ID único para el botón

        button.setOnClickListener { btn ->
            val oldId = idOfAudioPlaying

            // Detener audio si hay algo reproduciéndose
            if (idOfAudioPlaying != -1 && ttsManager.getIsPlaying()) {
                ttsManager.stop()
                (context as? android.app.Activity)?.findViewById<ImageButton>(idOfAudioPlaying)
                    ?.setImageResource(R.drawable.play)
                (context as? android.app.Activity)?.findViewById<ImageButton>(idOfAudioPlaying)
                    ?.setBackgroundResource(R.drawable.round_button_background)
                idOfAudioPlaying = -1
            }

            // Reproducir nuevo audio si es un botón diferente
            if (oldId != button.id && text.isNotEmpty()) {
                speakText(text, button.id)
                idOfAudioPlaying = button.id
                (context as? android.app.Activity)?.findViewById<ImageButton>(idOfAudioPlaying)
                    ?.setImageResource(R.drawable.pause)
                (context as? android.app.Activity)?.findViewById<ImageButton>(idOfAudioPlaying)
                    ?.setBackgroundResource(R.drawable.round_button_background)
            }
        }
    }

    fun changeSpeed(spinner: Spinner){
        spinner.id = View.generateViewId() // Genera un ID único para el spinner

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
    // Función para leer el texto
    private fun speakText(text: String, btnId:Int) {
        ttsManager.speak(text,btnId, "InfoActivityUtterance")
    }
}
