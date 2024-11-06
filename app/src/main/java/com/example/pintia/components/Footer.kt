package com.example.pintia.components

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.GalleryActivity
import com.example.pintia.InfoActivity
import com.example.pintia.MainMap
import com.example.pintia.MapActivity
import com.example.pintia.R
import com.example.pintia.SettingsActivity

class Footer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {


    private val footerEntries: List<Pair<Int, Class<out AppCompatActivity>>> = listOf(
        Pair(R.drawable.info, InfoActivity::class.java),
        Pair(R.drawable.map, MapActivity::class.java),
        Pair(R.drawable.home, MainMap::class.java),
        Pair(R.drawable.gallery, GalleryActivity::class.java),
        Pair(R.drawable.settings, SettingsActivity::class.java)
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.component_footer, this, true)

        val inflater = LayoutInflater.from(context)
        val linearLayout = findViewById<LinearLayout>(R.id.footer_main_layout)

        linearLayout.removeAllViews()

        linearLayout.weightSum = footerEntries.size.toFloat()

        footerEntries.forEach { (drawable, activity) ->
            val layout = inflater.inflate(
                R.layout.component_footer_entry,
                linearLayout,
                false
            ) as RelativeLayout
            val selected = activity.simpleName.equals(context::class.simpleName)

            val layoutParams = layout.layoutParams
            if (selected) {
                layoutParams.height = resources.getDimensionPixelSize(R.dimen.active_button_size)
            }
            layout.layoutParams = layoutParams


            val item = newImageButton(selected, activity)
            item.setImageResource(drawable)

            layout.addView(item)
            linearLayout.addView(layout)
            layout.requestLayout()
        }
    }

    private fun newImageButton(
        selected: Boolean,
        activity: Class<out AppCompatActivity>
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
        imageButton.setOnClickListener {
            val intent = Intent(context, activity)
            context.startActivity(intent)
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