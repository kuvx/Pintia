package com.example.pintia.puntosPrincipales.lasQuintanasViews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.pintia.R
import com.example.pintia.components.Header
import com.example.pintia.services.TTSManager
import com.example.pintia.services.DynamicViewBuilder.pueblaActivity
import com.example.pintia.utils.TutorialManager
import com.example.pintia.utils.TutorialStep
import java.text.Normalizer

class YacimientoInfoFragment : Fragment() {
    companion object {
        private const val ARG_TITULO = "param1"
        private const val ARG_PATH = "param2"

        fun newInstance(titulo: String, path: String): YacimientoInfoFragment {
            val fragment = YacimientoInfoFragment()
            val args = Bundle()
            args.putString(ARG_TITULO, titulo)
            args.putString(ARG_PATH, path)
            fragment.arguments = args
            return fragment
        }
    }

    // Variables de instancia para los parámetros
    private var titulo: String? = null
    private var path: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recuperar y asignar los argumentos a variables de instancia
        arguments?.let {
            titulo = it.getString(ARG_TITULO)
            path = it.getString(ARG_PATH)
        }
    }

    private lateinit var ttsManager: TTSManager
    private var idOfAudioPlaying = -1

    private lateinit var tutorialOverlay: FrameLayout
    private lateinit var tutorialManager: TutorialManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_info_views, container, false)

        val context = requireContext()
        val activity = requireActivity()

        val titulo = this.titulo ?: "title"
        val path = this.path ?: "path"

        activity
            .findViewById<Header>(R.id.header)
            .title = titulo
        // Recuperar los datos del Intent

        var layout: RelativeLayout = rootView.findViewById(R.id.component_info_views)
        ttsManager = TTSManager(context) { success ->
            if (success) {
                // TTS inicializado correctamente
                ttsManager.setupListener(activity, idOfAudioPlaying)
            } else {
                // Manejar el error de inicialización
            }
        }

        // Carga los datos desde el JSON
        println(path)
        var tituloCod = ""
        when (path) {
            "ruedas" -> {
                tituloCod = titulo.split(".")[0]
            }

            "quintana" -> {
                tituloCod = titulo.lowercase().replace(" ", "_")
                tituloCod = Normalizer.normalize(tituloCod, Normalizer.Form.NFD)
                    .replace(Regex("\\p{M}"), "")

            }

        }
        pueblaActivity(layout, context, path, tituloCod, ttsManager)

        tutorialOverlay = rootView.findViewById(R.id.tutorialOverlay)

        // Lista de pasos del tutorial
        val tutorialSteps = listOf(
            TutorialStep(
                R.drawable.tutorial_audio,
                getString(R.string.tut_audio),
                getString(R.string.tut_audio_desc)
            )
        )

        // Inicializar TutorialManager
        tutorialManager = TutorialManager(activity, tutorialOverlay, tutorialSteps)

        // Mostrar tutorial si es la primera vez
        if (TutorialManager.isFirstTimeTutorial(activity)) {
            tutorialManager.showTutorial()
        }

        return rootView
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