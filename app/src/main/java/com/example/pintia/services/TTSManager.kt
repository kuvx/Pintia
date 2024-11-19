package com.example.pintia.services

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class TTSManager (private val context: Context, private val onTTSInit:
    (Boolean) -> Unit) : TextToSpeech.OnInitListener  {

    private var isSpeaking: Boolean = false
    private lateinit var tts: TextToSpeech
    private var isInitialized = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val lan = context.resources.configuration.locales[0].toString().split("_")
            val locale = Locale(lan[0], this.getDefaultRegionForLanguage(lan[0]))

            val result = tts.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTSManager", "Language not supported")
                onTTSInit(false)
            } else {
                isInitialized = true
                tts.setSpeechRate(0.75f)
                tts.setPitch(1.0f)
                onTTSInit(true)
            }
        } else {
            Log.e("TTSManager", "TTS Initialization failed")
            onTTSInit(false)
        }
    }

    fun speak(text: String, utteranceId: String) {
        if (isInitialized) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            isSpeaking=true
        } else {
            Log.e("TTSManager", "TTS not initialized")
        }
    }

    fun stop() {
        if (isInitialized) {
            tts.stop()
            isSpeaking=false
        }
    }

    fun setUtteranceProgressListener(listener: UtteranceProgressListener) {
        tts.setOnUtteranceProgressListener(listener)
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }

    private fun getDefaultRegionForLanguage(languageCode: String): String {
        // Lista de idiomas con sus regiones predeterminadas
        val defaultRegions = mapOf(
            "es" to "ES", // Español - España
            "en" to "US", // Inglés - Estados Unidos
            "fr" to "FR", // Francés - Francia
            "de" to "DE", // Alemán - Alemania
        )

        // Devolver la región predeterminada si existe, o un valor alternativo
        return defaultRegions[languageCode] ?: Locale(languageCode).country.ifEmpty { "US" }
    }

    fun getIsPlaying():Boolean {
        return isSpeaking
    }
}