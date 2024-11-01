package com.example.pintia.components

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.pintia.MainMap
import com.example.pintia.R

class Header @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : LinearLayout(context, attrs, defStyleAttr) {

    var title:String = ""
        set(value) {
            field = value
            updateTitle()
        }

    private fun updateTitle() {
        val titleComponent:TextView = findViewById(R.id.header_title)
        titleComponent.text = context.getString(R.string.header, title)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.component_header, this, true)
        val backButton:ImageButton = findViewById(R.id.back_button_header)
        backButton.setOnClickListener {
            Toast.makeText(context, "Botton redondo presionado", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, MainMap::class.java)
            context.startActivity(intent)
        }
    }
}