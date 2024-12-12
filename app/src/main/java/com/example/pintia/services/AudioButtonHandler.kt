package com.example.pintia.services

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import com.example.pintia.R

class AudioButtonHandler(
    private val context: Context,
    private val ttsManager: TTSManager
) {

    // Variable para rastrear qué botón está reproduciendo y que texto
    private var idOfAudioPlaying: Int = -1
    private var textToSpeech: String = ""

    // Variables de control de velocidad
    private val initialPosition = 2
    private var position = initialPosition
    private val speeds = arrayOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)


    private fun stopSpeech() {
        ttsManager.stop()
        (context as? android.app.Activity)?.findViewById<ImageButton>(idOfAudioPlaying)
            ?.setImageResource(R.drawable.play)
        (context as? android.app.Activity)?.findViewById<ImageButton>(idOfAudioPlaying)
            ?.setBackgroundResource(R.drawable.round_button_background)
        idOfAudioPlaying = -1
        textToSpeech = ""
    }

    private fun startSpeech(id: Int, text: String) {
        speakText(text, id)
        idOfAudioPlaying = id
        textToSpeech = text
        (context as? android.app.Activity)?.findViewById<ImageButton>(idOfAudioPlaying)
            ?.setImageResource(R.drawable.pause)
        (context as? android.app.Activity)?.findViewById<ImageButton>(idOfAudioPlaying)
            ?.setBackgroundResource(R.drawable.round_button_background)
    }

    /**
     * Configura un botón para manejar la reproducción de audio.
     * @param button El botón (ImageButton) que se usará para reproducir audio.
     * @param text El texto que debe ser leído en voz alta al presionar el botón.
     */
    fun setupAudioButton(button: ImageButton, text: String, speedButton: Button) {
        button.id = View.generateViewId() // Genera un ID único para el botón

        button.setOnClickListener { _ ->
            val oldId = idOfAudioPlaying

            // Detener audio si hay algo reproduciéndose
            if (idOfAudioPlaying != -1 && ttsManager.getIsPlaying()) {
                stopSpeech()
            }

            // Sincronizar la velocidad
            syncSpeed(speedButton, true)

            // Reproducir nuevo audio si es un botón diferente
            if (oldId != button.id && text.isNotEmpty()) {
                startSpeech(button.id, text)
            }
        }
    }

    /**
     * Configura la velocidad de lectura de texto a voz (TTS) y actualiza el texto del botón de
     * velocidad.
     *
     * @param speedButton El botón cuyo texto indica la velocidad actual. Se actualiza con la nueva velocidad.
     * @param fromButton (Opcional) Indica si la velocidad se selecciona a partir del texto actual del botón.
     *                   Si es `true`, se utiliza el texto del botón para determinar la velocidad.
     *                   Predeterminado es `false`.
     * @param updateOnlyText (Opcional) Si es `true`, solo se actualiza el texto del botón y no se cambia
     *                       la velocidad del TTS. Predeterminado es `false`.
     * @param initial (Opcional) Si es `true`, se selecciona el valor por defecto (`initialPosition`),
     *                  en caso contrario se usará la configuración determinada por `fromButton`
     */
    private fun syncSpeed(
        speedButton: Button,
        fromButton: Boolean = false,
        updateOnlyText: Boolean = false,
        initial: Boolean = false
    ) {
        // Determinar el índice de velocidad a utilizar
        val index = if (initial) {
            // Usar la velocidad por defecto
            initialPosition
        } else if (fromButton) {
            // Extraer la velocidad del texto del botón
            val newPosition = speeds.indexOf(
                // Eliminar el prefijo 'x' y convertir a float
                speedButton.text.toString().substring(1).toFloat()
            )
            position = newPosition
            newPosition
        } else {
            // Incrementar la posición circularmente dentro del rango de velocidades disponibles
            (++position) % speeds.size
        }

        // Obtener la velocidad seleccionada
        val selectedSpeed = speeds[index]
        // Formatear el texto del botón con el prefijo "x" seguido de la velocidad
        val buttonText = "x${selectedSpeed}"
        speedButton.text = buttonText

        if (!updateOnlyText) {
            // Cambiar la velocidad del motor TTS
            ttsManager.setSpeechRate(selectedSpeed)
        }
    }


    fun changeSpeed(button: Button, statusButton: ImageButton) {
        button.id = View.generateViewId() // Genera un ID único para el spinner
        syncSpeed(button, initial = true)

        button.setOnClickListener {
            val oldId = idOfAudioPlaying
            val tts = textToSpeech

            if (oldId != statusButton.id) {
                // Si es otro botón diferente al suyo solo cambiamos el texto
                syncSpeed(button, updateOnlyText = true)

            } else {
                stopSpeech()
                syncSpeed(button)

                // Si ya estaba en reproducción se reinicia
                if (oldId != -1) startSpeech(oldId, tts)

            }
        }
    }

    // Función para leer el texto
    private fun speakText(text: String, btnId: Int) {
        ttsManager.speak(text, btnId, "InfoActivityUtterance")
    }
}
