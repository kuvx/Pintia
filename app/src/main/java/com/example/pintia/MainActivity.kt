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
    private var disableTimer = false

    private fun closeLogin() {
        if (change) return

        change = true
        disableTimer = true

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
        supportFragmentManager.beginTransaction()
            .replace(id, fragment)
            .addToBackStack(fragment::class.simpleName)
            .commit()
    }

    fun changeFragment(fragment: Fragment) {
        replaceFrame(R.id.fragment_container, fragment)
    }

    fun goBack() {
        val fragmentManager = supportFragmentManager

        val backStackCount = fragmentManager.backStackEntryCount

        val lastEntryName = fragmentManager.getBackStackEntryAt(backStackCount - 2).name

        // Si llegamos a la entrada que generó template fragment avanzamos a la pantalla inicial
        // cambiamos al main, aunque sea menos eficiente es más simple
        if (lastEntryName == TemplateFragment::class.simpleName)
            changeMain()
        else
            fragmentManager.popBackStack()

    }

    private fun changeMain(timer: Boolean = false) {
        replaceFrame(R.id.main, MainFragment())
        change = false
        if (timer) disableTimer = false

        val mainLayout = findViewById<RelativeLayout>(R.id.main)

        // Cambiar a siguiente actividad por timeout o click

        mainLayout.setOnClickListener {
            closeLogin()
        }

        if (timer) {
            Handler(Looper.getMainLooper()).postDelayed({
                Toast.makeText(this, "TIMEOUT!", Toast.LENGTH_SHORT).show()
                if (!disableTimer)
                    closeLogin()
            }, 1500) // 1000 milisegundos = 1 segundo
        }
    }
}