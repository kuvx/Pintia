package com.example.pintia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.pintia.components.Header

class TemplateFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_template, container, false)

        val header = rootView.findViewById<Header>(R.id.header)

        val btnRequest = header.findViewById<ImageButton>(R.id.button_reserva)
        btnRequest.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.changeFrame(RequestVisitFragment())
        }

        return rootView
    }
}