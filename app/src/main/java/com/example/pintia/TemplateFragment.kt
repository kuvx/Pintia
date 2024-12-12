package com.example.pintia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pintia.components.Footer

class TemplateFragment : Fragment() {

    private val fragmentList = listOf(
        R.drawable.info to InfoFragment(),
        R.drawable.map to MapFragment(),
        R.drawable.home to MainMapVFragment(),
        R.drawable.gallery to GalleryVFragment(),
        R.drawable.settings to SettingsVFragment()
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_template, container, false)

        val footer = rootView.findViewById<Footer>(R.id.footer)
        footer.setFooter(fragmentList)
        return rootView
    }
}