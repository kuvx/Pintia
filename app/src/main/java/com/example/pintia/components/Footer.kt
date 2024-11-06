package com.example.pintia.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.GalleryActivity
import com.example.pintia.InfoActivity
import com.example.pintia.MainActivity
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
            val item: ImageButton = inflater.inflate(
                R.layout.component_footer_entry,
                linearLayout,
                false
            ) as ImageButton

            item.setImageResource(drawable)
            linearLayout.addView(item)

            item.setOnClickListener {
                val intent = Intent(context, activity)
                context.startActivity(intent)
            }

            if (activity.simpleName.equals(context::class.simpleName)) {
                // Reajuste para indicar que está seleccionado
                item.layoutParams.width =
                    resources.getDimensionPixelSize(R.dimen.active_button_size)
                item.layoutParams.height =
                    resources.getDimensionPixelSize(R.dimen.active_button_size)
                item.setBackgroundResource(R.drawable.round_button_selected_background)
            } else {
                item.layoutParams.width =
                    resources.getDimensionPixelSize(R.dimen.default_button_size)
                item.layoutParams.height =
                    resources.getDimensionPixelSize(R.dimen.default_button_size)
            }
            item.requestLayout()
        }
    }
}