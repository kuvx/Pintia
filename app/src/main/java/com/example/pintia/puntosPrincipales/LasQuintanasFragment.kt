package com.example.pintia.puntosPrincipales

import android.os.Bundle
import com.example.pintia.R
import com.example.pintia.components.Header
import android.graphics.PointF
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pintia.MainActivity
import com.example.pintia.models.PoligonoColor
import com.example.pintia.services.OnPoligonoTouchListener
import com.example.pintia.services.OverlayView

class LasQuintanasFragment : Fragment(), OnPoligonoTouchListener {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.activity_las_quintanas, container, false)

        requireActivity()
            .findViewById<Header>(R.id.header)
            .title = getString(R.string.quinanas)


        val overlayView: OverlayView = rootView.findViewById(R.id.overlayView)

        val poligonos = listOf(
            PoligonoColor(
                puntos = listOf(
                    PointF(224f, 535f),
                    PointF(580f, 508f),
                    PointF(722f, 704f),
                    PointF(224f, 729f)
                ),
                title = getString(R.string.sigloIV)
            ),
            PoligonoColor(
                puntos = listOf(
                    PointF(65f, 753f),
                    PointF(0f, 1400f),
                    PointF(891f, 923f),
                    PointF(722f, 720f)
                ),
                title = getString(R.string.sigloI)
            ),
            PoligonoColor(
                puntos = listOf(
                    PointF(0f, 1430f),
                    PointF(0f, 1654f),
                    PointF(1040f, 1654f),
                    PointF(1040f, 1080f),
                    PointF(911f, 953f)
                ),
                title = getString(R.string.sigloII)
            ),
            PoligonoColor(
                puntos = listOf(
                    PointF(723f, 635f),
                    PointF(855f, 597f),
                    PointF(1040f, 700f),
                    PointF(1040f, 933F)
                ),
                title = getString(R.string.sigloIII)
            ),
            PoligonoColor(
                puntos = listOf(
                    PointF(224f, 355f),
                    PointF(580f, 355f),
                    PointF(660f, 440f),
                    PointF(224f, 460f)
                ),
                title = getString(R.string.pozo)
            )
        )

        // Asigna la lista de polígonos a OverlayView para que los dibuje
        overlayView.poligonos = poligonos
        // Asigna el listener
        overlayView.onPoligonoTouchListener = this

        return rootView
    }

    // Implementación del listener para cuando se toca un polígono
    override fun onPoligonoTouched(poligono: PoligonoColor) {
        Toast.makeText(
            requireContext(),
            "Tocaste un polígono: ${poligono.title}",
            Toast.LENGTH_SHORT
        ).show()
        (requireActivity() as MainActivity).changeFrame(poligono.destinationFragment)
    }
}