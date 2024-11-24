package com.example.pintia.services

import android.app.Activity
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class TTSManager (private val context: Context, private val onTTSInit:
    (Boolean) -> Unit) : TextToSpeech.OnInitListener  {

    private var isSpeaking: Boolean = false
    private var tts: TextToSpeech = TextToSpeech(context, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = context.resources.configuration.locales[0]

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

    fun speak(text: String, utteranceId: String, onDoneCallback: (() -> Unit)? = null) {
        if (isInitialized) {
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    isSpeaking = true
                }

                override fun onDone(utteranceId: String?) {
                    isSpeaking = false
                    // Llama al callback en el hilo principal
                    onDoneCallback?.let {
                        (context as? Activity)?.runOnUiThread {
                            it.invoke() // Ejecuta el callback
                        }
                    }
                }

                override fun onError(utteranceId: String?) {
                    Log.e("TTSManager", "Error while speaking")
                    isSpeaking = false
                }
            })
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
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

    fun getIsPlaying():Boolean {
        return isSpeaking
    }
}