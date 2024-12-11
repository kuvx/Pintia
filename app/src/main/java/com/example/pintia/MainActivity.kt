package com.example.pintia

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pintia.components.Footer
import com.example.pintia.components.Header
import com.example.pintia.utils.settings.FontSizeUtils
import com.example.pintia.utils.settings.LanguageUtils

class MainActivity : AppCompatActivity() {

    // Flag para indicar que se ha realizado la transición y en caso de que se suceda otra se ignore
    private var change = false
    // Flag para deshabilitar el cambio de fragmento por el temporizador
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
        changeMain(init = true, timer = true)

        // Ejecuta ajustes de idioma y tamaño de fuente
        findViewById<View>(R.id.main).post {
            val viewGroup = findViewById<View>(R.id.main)
            FontSizeUtils.applySavedFontSize(viewGroup, this)
            LanguageUtils.applySavedLanguage(viewGroup)
        }
    }

    private fun replaceFrame(id: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(id, fragment)
            .addToBackStack(fragment::class.simpleName)
            .commit()
    }

    /**
     * Cambia el fragmento actual, contenido entre el header y footer, por el nuevo pasado por
     * parámetro
     * @param fragment nuevo fragmento a colocar
     */
    fun changeFragment(fragment: Fragment) {
        replaceFrame(R.id.fragment_container, fragment)
    }

    /**
     * Regresa al anterior fragmento de la pila de transiciones
     */
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

    private fun changeMain(timer: Boolean = false, init:Boolean = false) {
        // Si el cambio al fragmento no es la inicial se carga de nuevo el fragmento
        if (!init)
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
                if (!disableTimer) closeLogin()
            }, 1500) // 1000 milisegundos = 1 segundo
        }
    }

    /**
     * Consulta el nombre del fragmento actual almacenado en la pila de transiciones, en caso de no
     * haber ningúno se devolverá `"No fragment"`
     */
    fun getActualFragment(): String {
        val backStackCount = supportFragmentManager.backStackEntryCount

        return if (backStackCount > 0)
            supportFragmentManager.getBackStackEntryAt(backStackCount - 1).name
                ?: "No fragment name"
        else "No fragment"
    }

    /**
     * Actualiza el titulo del header siempre y cuando el fragmento activo no sea el main
     *
     * @param title titulo del header a establecer
     */
    fun updateHeader(title: String) {
        if (getActualFragment() == MainFragment::class.simpleName) return
        findViewById<Header>(R.id.header).title = title
        findViewById<Footer>(R.id.footer).updateFooter()
    }
}