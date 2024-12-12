package com.example.pintia.puntosPrincipales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.pintia.MainActivity
import com.example.pintia.R
import com.example.pintia.services.DynamicViewBuilder.pueblaActivity
import com.example.pintia.services.TTSManager


class EdificioUVaVFragment : Fragment() {

    private lateinit var ttsManager: TTSManager
    private var idOfAudioPlaying = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_info_views, container, false)

        (requireActivity() as MainActivity).updateHeader(getString(R.string.titulo_principal))

        var layout: RelativeLayout = rootView.findViewById(R.id.component_info_views)
        ttsManager = TTSManager(requireContext()) { success ->
            if (success) {
                // TTS inicializado correctamente
                ttsManager.setupListener(requireActivity(), idOfAudioPlaying)
            }
        }

        val path = "edificio"
        pueblaActivity(layout,requireContext(), path, path,ttsManager)

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