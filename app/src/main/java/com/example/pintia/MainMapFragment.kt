package com.example.pintia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.pintia.puntosPrincipales.EdificioUVaVFragment
import com.example.pintia.puntosPrincipales.LasQuintanasVFragment
import com.example.pintia.puntosPrincipales.LasRuedasVFragment
import com.example.pintia.puntosPrincipales.MurallaAsedioVFragment

class MainMapFragment : Fragment() {

    private fun getMain(): MainActivity {
        return requireActivity() as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_main_map, container, false)

        (requireActivity() as MainActivity).updateHeader(getString(R.string.home))

        val btnRequest = rootView.findViewById<LinearLayout>(R.id.reserva_btn)
        btnRequest.setOnClickListener {
            getMain().changeFragment(RequestVisitFragment())
        }


        // Pares id, frame objetivo
        val fragments = mapOf(
            R.id.button1 to LasQuintanasVFragment(),
            R.id.button2 to MurallaAsedioVFragment(),
            R.id.button3 to LasRuedasVFragment(),
            R.id.button4 to EdificioUVaVFragment()
        )

        fragments.forEach { (id, fragment) ->
            rootView.findViewById<LinearLayout>(id)
                .setOnClickListener {
                    getMain().changeFragment(fragment)
                }
        }

        return rootView
    }
}