package com.example.pintia.components

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

    private val buttons: Array<ImageButton> by lazy {
        arrayOf(
            findViewById(R.id.info_button),
            findViewById(R.id.map_button),
            findViewById(R.id.home_button),
            findViewById(R.id.media_button),
            findViewById(R.id.settings_button)
        )
    }

    fun changeView(index:Int) {
        val activityClass: Class<out AppCompatActivity> = when (index) {
            0 -> InfoActivity::class.java
            1 -> MapActivity::class.java
            2 -> MainMap::class.java
            3 -> GalleryActivity::class.java
            4 -> SettingsActivity::class.java
            // No debería llegar aquí
            else -> MainActivity::class.java
        }
        val intent = Intent(context, activityClass)
        context.startActivity(intent)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.component_footer, this, true)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                changeView(index)
            }
        }

    }
}