package com.example.pintia.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.pintia.GalleryFragment
import com.example.pintia.InfoFragment
import com.example.pintia.MainActivity
import com.example.pintia.MainMapFragment
import com.example.pintia.MapFragment
import com.example.pintia.R
import com.example.pintia.SettingsFragment

class Footer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {


    private val footerEntries = listOf(
        R.drawable.info to InfoFragment(),
        R.drawable.map to MapFragment(),
        R.drawable.home to MainMapFragment(),
        R.drawable.gallery to GalleryFragment(),
        R.drawable.settings to SettingsFragment()
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.component_footer, this, true)
        setFooter()
    }

    /**
     * Recrea el footer ajustando las entradas comprobando si el frame actual es una de ella y
     * marcarla como seleccionada
     */
    fun setFooter() {
        val inflater = LayoutInflater.from(context)
        val linearLayout = findViewById<LinearLayout>(R.id.footer_main_layout)
        linearLayout.removeAllViews()

        linearLayout.weightSum = footerEntries.size.toFloat()

        footerEntries.forEach { (drawable, fragment) ->
            val layout = inflater.inflate(
                R.layout.component_footer_entry,
                linearLayout,
                false
            ) as RelativeLayout

            // El fragmento actual es la entrada que se comprueba
            val selected = fragment::class.simpleName ==
                    (context as MainActivity).getActualFragment()

            val layoutParams = layout.layoutParams
            if (selected) {
                layoutParams.height = resources.getDimensionPixelSize(R.dimen.active_button_size)
            }
            layout.layoutParams = layoutParams


            val item = newImageButton(selected, fragment)
            item.setImageResource(drawable)

            layout.addView(item)
            linearLayout.addView(layout)
            layout.requestLayout()
        }
    }

    private fun newImageButton(
        selected: Boolean,
        fragment: Fragment
    ): ImageButton {
        // Crear una nueva instancia de ImageButton
        val imageButton = ImageButton(context)

        imageButton.scaleType = ImageView.ScaleType.CENTER_INSIDE
        imageButton.adjustViewBounds = true


        val resource =
            if (selected)
                R.drawable.round_button_selected_background
            else
                R.drawable.round_button_background
        imageButton.setBackgroundResource(resource)

        // Configura un OnClickListener para el botón
        if (!selected) {
            imageButton.setOnClickListener {
                (context as MainActivity).changeFragment(fragment)
            }
        }

        val size =
            if (selected) resources.getDimensionPixelSize(R.dimen.active_button_size)
            else RelativeLayout.LayoutParams.WRAP_CONTENT

        val layoutParams = LayoutParams(size, size).apply {
            weight = 1f
            gravity = Gravity.CENTER_HORIZONTAL
        }

        imageButton.layoutParams = layoutParams
        imageButton.requestLayout()
        return imageButton
    }

}