package com.example.pintia.components

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.pintia.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.pintia.R

/**
 * Para q sea mas facil la informacion contenida en la leyenda sera obtenida del back
 * Y los diferentes botones seran generados en este mismo archivo en funcion de los valores
 */
class Leyenda @JvmOverloads constructor(
    context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : LinearLayout(context, attrs, defStyleAttr) {

    private val points: Array<TextView> by lazy{
        arrayOf(
            findViewById(R.id.point_1),
            findViewById(R.id.point_2),
            findViewById(R.id.point_3),
            findViewById(R.id.point_4),
            findViewById(R.id.point_5)
        )
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.component_leyenda, this, true)
        val toggleButton: FloatingActionButton = findViewById(R.id.toggleButton)
        val legendCardView: CardView = findViewById(R.id.legendCardView)

        points.forEachIndexed { index, button ->
            button.setOnClickListener {
                changeView(index)
            }
        }

        toggleButton.setOnClickListener {
            // Cambiar la visibilidad del CardView
            if (legendCardView.visibility == View.GONE) {
                legendCardView.visibility = View.VISIBLE
                toggleButton.setImageResource(R.drawable.retract)
            } else {
                legendCardView.visibility = View.GONE
                toggleButton.setImageResource(R.drawable.expand)
            }
        }
    }

    fun changeView(index:Int) {
        val activityClass: Class<out AppCompatActivity> = when (index) {
            0 -> MainActivity::class.java
            /*1 -> RuedasMapPointActivity::class.java
            2 -> MurallaPointActivity::class.java
            3 -> CatapultasPointActivity::class.java
            4 -> YacimientoPointActivity::class.java
            // No debería llegar aquí*/
            else -> MainActivity::class.java
        }
        val intent = Intent(context, activityClass)
        context.startActivity(intent)
    }
}