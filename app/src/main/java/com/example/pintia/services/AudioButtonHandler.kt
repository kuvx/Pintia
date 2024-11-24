package com.example.pintia.services

import android.content.Context
import android.view.View
import android.widget.ImageButton
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
                    ?.setBackgroundResource(R.drawable.round_button_background)
                idOfAudioPlaying = -1
            }

            // Reproducir nuevo audio si es un botón diferente
            if (oldId != button.id && text.isNotEmpty()) {
                speakText(text)
                idOfAudioPlaying = button.id
                btn.setBackgroundResource(R.drawable.round_button_selected_background)
            }
        }
    }
    // Función para leer el texto
    private fun speakText(text: String) {
        ttsManager.speak(text, "InfoActivityUtterance")
    }
}
