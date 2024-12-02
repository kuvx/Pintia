    package com.example.pintia.services

    import android.app.Activity
    import android.content.Context
    import android.speech.tts.TextToSpeech
    import android.speech.tts.UtteranceProgressListener
    import android.util.Log
    import android.widget.ImageButton
    import com.example.pintia.R
    import java.util.Locale

    class TTSManager (private val context: Context, private val onTTSInit:
        (Boolean) -> Unit) : TextToSpeech.OnInitListener  {

        private var isSpeaking: Boolean = false
        private var tts: TextToSpeech = TextToSpeech(context, this)
        private var isInitialized = false
        private lateinit var activity: Activity

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

        fun speak(text: String,btnId:Int, utteranceId: String, onDoneCallback: (() -> Unit)? = null) {
            if (isInitialized) {
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        isSpeaking = true
                    }

                    override fun onDone(utteranceId: String?) {
                        isSpeaking = false
                        activity.findViewById<ImageButton>(btnId).setImageResource(R.drawable.audio)
                        activity.findViewById<ImageButton>(btnId).setBackgroundResource(R.drawable.round_button_background)
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

        fun setSpeechRate(speed: Float) {
            if (isInitialized) {
                tts.setSpeechRate(speed)
            } else {
                Log.e("TTSManager", "TTS no está inicializado. No se puede ajustar la velocidad.")
            }
        }

        fun setupListener(activity: Activity, idOfAudioPlaying: Int) {
            this.activity=activity
            this.setUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    println("Iniciando lectura...")
                    activity.findViewById<ImageButton>(idOfAudioPlaying).setImageResource(R.drawable.pause)
                }

                override fun onDone(utteranceId: String?) {
                    activity.runOnUiThread {
                        println("Lectura finalizada.")
                        activity.findViewById<ImageButton>(idOfAudioPlaying).setImageResource(R.drawable.audio)
                        activity.findViewById<ImageButton>(idOfAudioPlaying).setBackgroundResource(R.drawable.round_button_background)
                    }
                }

                override fun onError(utteranceId: String?) {
                    println("Error al leer el texto.")
                }
            })
        }
    }