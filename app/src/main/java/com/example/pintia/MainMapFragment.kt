package com.example.pintia

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.pintia.puntosPrincipales.EdificioUVaFragment
import com.example.pintia.puntosPrincipales.LasQuintanasFragment
import com.example.pintia.puntosPrincipales.LasRuedasFragment
import com.example.pintia.puntosPrincipales.MurallaAsedioFragment

class MainMapFragment : Fragment() {

    private lateinit var gridLayout: GridLayout

    private fun getMain(): MainActivity {
        return requireActivity() as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_main_map, container, false)

        // Inicializamos el GridLayout
        gridLayout = rootView.findViewById(R.id.buttonGrid)

        // Ajuste de columnas según la orientación actual
        adjustGridLayoutForOrientation(resources.configuration.orientation)

        // Actualizar el encabezado de la actividad
        (requireActivity() as MainActivity).updateHeader(getString(R.string.home))

        // Botón de reserva
        val btnRequest = rootView.findViewById<LinearLayout>(R.id.reserva_btn)
        btnRequest.setOnClickListener {
            getMain().changeFragment(RequestVisitFragment())
        }

        // Pares de id de botones y fragmentos asociados
        val fragments = mapOf(
            R.id.button1 to LasQuintanasFragment(),
            R.id.button2 to MurallaAsedioFragment(),
            R.id.button3 to LasRuedasFragment(),
            R.id.button4 to EdificioUVaFragment()
        )

        // Asignar los fragmentos a los botones
        fragments.forEach { (id, fragment) ->
            rootView.findViewById<LinearLayout>(id)
                .setOnClickListener {
                    // Verificamos si el fragmento está adjunto antes de realizar la acción
                    if (isAdded) {
                        getMain().changeFragment(fragment)
                    }
                }
        }

        return rootView
    }

    // Método para ajustar las columnas del GridLayout según la orientación
    private fun adjustGridLayoutForOrientation(orientation: Int) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Cambiar a una sola fila (1x4) en paisaje
            gridLayout.columnCount = 4
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Cambiar a 2x2 en retrato
            gridLayout.columnCount = 2
        }
    }

    // Cambiar las columnas cuando la orientación cambie
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        adjustGridLayoutForOrientation(newConfig.orientation)
    }
}
