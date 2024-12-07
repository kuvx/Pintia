package com.example.pintia

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private var change = false

    private fun closeLogin(text: String) {
        if (change) return
        change = true
        Toast.makeText(this, "Change $text", Toast.LENGTH_SHORT).show()
        replaceFrame(R.id.main, TemplateFragment())
        replaceFrame(R.id.fragment_container, MainMapFragment())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main)
        window.navigationBarColor = resources.getColor(R.color.primary, theme)
        changeMain(true)
    }

    private fun replaceFrame(id: Int, fragment: Fragment) {
        val transaccion = supportFragmentManager.beginTransaction()
        transaccion.replace(id, fragment)
        transaccion.addToBackStack(null)
        transaccion.commit()
    }

    fun changeFrame(fragment: Fragment) {
        replaceFrame(R.id.fragment_container, fragment)
    }

    fun goBack() {
        supportFragmentManager.popBackStack()
    }

    fun changeMain(timer: Boolean = false) {
        replaceFrame(R.id.main, MainFragment())
        change = false

        val mainLayout = findViewById<RelativeLayout>(R.id.main)

        // Cambiar a siguiente actividad por timeout o click

        mainLayout.setOnClickListener {
            closeLogin("click")
        }
        if (timer) {
            Handler(Looper.getMainLooper()).postDelayed({
                closeLogin("timeout")
            }, 3500) // 1000 milisegundos = 1 segundo
        }
    }
}