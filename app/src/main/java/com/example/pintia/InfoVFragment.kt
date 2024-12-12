package com.example.pintia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.pintia.services.DynamicViewBuilder.pueblaActivity
import com.example.pintia.services.TTSManager
import java.text.Normalizer

class InfoVFragment : Fragment() {

    private lateinit var ttsManager: TTSManager
    private var idOfAudioPlaying = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_info_views, container, false)

        (requireActivity() as MainActivity).updateHeader(getString(R.string.info))

        val layout: RelativeLayout = rootView.findViewById(R.id.component_info_views)

        val context = requireContext()
        ttsManager = TTSManager(context) { success ->
            if (success) {
                // TTS inicializado correctamente
                ttsManager.setupListener(requireActivity(), idOfAudioPlaying)
            }
        }

        val path = "aboutUs"
        var tituloCod = getString(R.string.info).lowercase().replace(" ", "_")
        tituloCod =
            Normalizer.normalize(tituloCod, Normalizer.Form.NFD).replace(Regex("\\p{M}"), "")

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