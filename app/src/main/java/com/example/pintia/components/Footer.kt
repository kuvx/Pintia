package com.example.pintia.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.pintia.MainActivity
import com.example.pintia.R
import com.example.pintia.models.FooterEntry

class Footer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var footerEntries: List<FooterEntry> = emptyList()

    init {
        LayoutInflater.from(context).inflate(R.layout.component_footer, this, true)
    }

    /**
     * Rellena el footer con las opciones proporcionadas.
     * Uso recomendado exclusivamente para la instanciación del footer, para responder al fragmento
     * actual requiere una posterior llamada a `Footer.updateFooter()`
     * @param entries lista de pares referencia al drawable y fragmento
     */
    fun setFooter(entries: List<Pair<Int, Fragment>>) {
        val localEntries = mutableListOf<FooterEntry>()

        val inflater = LayoutInflater.from(context)
        val linearLayout = findViewById<LinearLayout>(R.id.footer_main_layout)
        linearLayout.removeAllViews()
        linearLayout.weightSum = footerEntries.size.toFloat()

        entries.forEach { (drawable, fragment) ->
            val layout = inflater.inflate(
                R.layout.component_footer_entry,
                linearLayout,
                false
            ) as RelativeLayout

            val action = { _: View ->
                (context as MainActivity).changeFragment(fragment)
            }

            // Establecemos el click tanto en el layout como en el botón para ampliar el rango de
            // click y evitar que el usuario no note que se ha presionado el botón
            layout.setOnClickListener(action)
            val item = newImageButton(action)
            item.setImageResource(drawable)


            layout.addView(item)
            linearLayout.addView(layout)
            localEntries.add(
                FooterEntry(
                    fragment = fragment::class.simpleName!!,
                    button = item,
                    layout = layout
                )
            )
        }

        footerEntries = localEntries.toList()
        linearLayout.requestLayout()
    }

    /**
     * Actualiza el footer para que responda al fragmento actual.
     */
    fun updateFooter() {
        val actualFragment = (context as MainActivity).getActualFragment()

        footerEntries.forEach { (fragmentName, button, layout) ->
            val isSelected = fragmentName == actualFragment

            // Desactivamos o activamos los botones y layouts para evitar que el toque
            // no sea detectado
            layout.isEnabled = !isSelected
            button.isEnabled = !isSelected

            val resource = if (isSelected) {
                R.drawable.round_button_selected_background
            } else {
                R.drawable.round_button_background
            }
            button.setBackgroundResource(resource)

            val size = if (isSelected) {
                resources.getDimensionPixelSize(R.dimen.active_button_size)
            } else {
                LayoutParams.WRAP_CONTENT
            }

            // Actualización de h y w
            val layoutParams = button.layoutParams

            layoutParams.height = size
            layoutParams.width = size

            button.layoutParams = layoutParams
            button.requestLayout()
        }
    }

    private fun newImageButton(action: (View) -> Unit): ImageButton {
        val imageButton = ImageButton(context)

        imageButton.scaleType = ImageView.ScaleType.CENTER_INSIDE
        imageButton.adjustViewBounds = true
        imageButton.setBackgroundResource(R.drawable.round_button_background)

        imageButton.setOnClickListener(action)

        val layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            weight = 1f
            gravity = Gravity.CENTER_HORIZONTAL
        }

        imageButton.layoutParams = layoutParams
        imageButton.requestLayout()
        return imageButton
    }
}