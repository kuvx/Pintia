package com.example.pintia.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.pintia.MainActivity
import com.example.pintia.R
import com.example.pintia.RequestVisitFragment

class Header @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var title: String = ""
        set(value) {
            field = value
            updateTitle()
        }

    private fun updateTitle() {
        val titleComponent: TextView = findViewById(R.id.header_title)
        titleComponent.text = context.getString(R.string.header, title)

        updateRequestButton()
    }

    private val requestButton: ImageButton

    private fun updateRequestButton() {
        val actualFragment = (context as MainActivity).getActualFragment()

        // Deshabilitar el botón si el fragmento actual es el de reserva
        requestButton.isEnabled =
            actualFragment != RequestVisitFragment::class.simpleName
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.component_header, this, true)

        (findViewById<ImageButton>(R.id.back_button_header)!!)
            .setOnClickListener {
                (context as MainActivity).goBack()
            }

        requestButton = findViewById(R.id.button_reserva)

        requestButton.setOnClickListener {
            (context as MainActivity).changeFragment(RequestVisitFragment())
        }
    }
}

