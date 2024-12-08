package com.example.pintia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.pintia.components.Header
import com.example.pintia.puntosPrincipales.EdificioUVaFragment
import com.example.pintia.puntosPrincipales.LasQuintanasFragment
import com.example.pintia.puntosPrincipales.LasRuedasFragment
import com.example.pintia.puntosPrincipales.MurallaAsedioFragment

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

        requireActivity()
            .findViewById<Header>(R.id.header)
            .title = getString(R.string.home)


        val btnRequest = rootView.findViewById<LinearLayout>(R.id.reserva_btn)
        btnRequest.setOnClickListener {
            getMain().changeFragment(RequestVisitFragment())
        }


        // Pares id, frame objetivo
        val fragments = mapOf(
            R.id.button1 to LasQuintanasFragment(),
            R.id.button2 to MurallaAsedioFragment(),
            R.id.button3 to LasRuedasFragment(),
            R.id.button4 to EdificioUVaFragment()
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