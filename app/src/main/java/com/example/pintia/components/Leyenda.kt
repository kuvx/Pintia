package com.example.pintia.components

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.pintia.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.pintia.R
import com.example.pintia.RequestVisitActivity

/**
 * Para q sea mas facil la informacion contenida en la leyenda sera obtenida del back
 * Y los diferentes botones seran generados en este mismo archivo en funcion de los valores
 */
class Leyenda @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    // Pares Vista actividad/null por defecto (cada vista que lo use debería reemplazarlos)
    var myMap = mapOf(
        Pair("No debería aparecer", null),
        Pair("-- Prueba --", MainActivity::class.java),
        Pair("-- Prueba 2 --", RequestVisitActivity::class.java),
        Pair("Punto de partida", null),
        Pair("Ruedas", null),
        Pair("Muralla", null),
        Pair("Catapulta", null),
        Pair("Yacimiento", null),
    )
        set(value) {
            field = value
            updateMenuEntries()
        }


    init {
        LayoutInflater.from(context).inflate(R.layout.component_leyenda, this, true)

        // Obtiene el LinearLayout del layout principal donde se agregarán los TextViews
        val toggleButton: FloatingActionButton = findViewById(R.id.toggleButton)

        toggleButton.setOnClickListener {
            // Cambiar la visibilidad del CardView
            alterMenuVisibility()
        }

        updateMenuEntries()
    }

    private fun updateMenuEntries() {
        // Itera sobre la lista y agrega un TextView para cada elemento
        val inflater = LayoutInflater.from(context)
        val linearLayout = findViewById<LinearLayout>(R.id.leyend_layout)
        linearLayout.removeAllViews()

        for ((name, activity) in myMap) {
            // Infla el diseño del TextView desde item_text_view.xml
            val textView = inflater.inflate(R.layout.leyend_entry, linearLayout, false) as TextView
            textView.text = name // Establece el texto del TextView
            textView.setOnClickListener { // Cuando se clicke se cambia de vista
                if (activity != null) {
                    val intent = Intent(context, activity)
                    context.startActivity(intent)
                } else {
                    // Cerramos el desplegable
                    alterMenuVisibility(close = true)
                }
            }

            // Agrega el TextView al LinearLayout
            linearLayout.addView(textView)
        }
    }

    private fun alterMenuVisibility(open: Boolean = false, close: Boolean = false) {
        val legendCardView: CardView = findViewById(R.id.legendCardView)
        val toggleButton: FloatingActionButton = findViewById(R.id.toggleButton)
        val openMenu = legendCardView.visibility == View.GONE || open
        val closeMenu = legendCardView.visibility == View.VISIBLE || close
        if (openMenu) {
            legendCardView.visibility = View.VISIBLE
            toggleButton.setImageResource(R.drawable.retract)
        } else if (closeMenu) {
            legendCardView.visibility = View.GONE
            toggleButton.setImageResource(R.drawable.expand)
        }
    }
}