package com.example.pintia.puntosPrincipales

import android.os.Bundle
import com.example.pintia.R
import com.example.pintia.components.Header
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.pintia.services.DynamicViewBuilder.pueblaActivity
import com.example.pintia.services.TTSManager

class LasQuintanasFragment : Fragment() {
    private lateinit var ttsManager: TTSManager
    private var idOfAudioPlaying = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_info_views, container, false)
        val context = requireContext()

        val text = getString(R.string.quinanas)
        requireActivity().findViewById<Header>(R.id.header)
            .title = text

        val layout: RelativeLayout = rootView.findViewById(R.id.component_info_views)

        //cambiamos el fondo
        layout.setBackgroundResource(R.drawable.fondo_quintana)
        ttsManager = TTSManager(context) { success ->
            if (success) {
                // TTS inicializado correctamente
                ttsManager.setupListener(requireActivity(), idOfAudioPlaying)
            } else {
                // Manejar el error de inicialización
            }
        }


        val path = "quintana"
        val tituloCod = text.lowercase().replace(" ", "_")

        pueblaActivity(layout, context, path, tituloCod, ttsManager)

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