package com.example.pintia

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.components.Header
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var settings : SettingsFragment;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.settings)
        settings = SettingsFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, settings)
            .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey)

            val languagePref = findPreference<ListPreference>("language")
            val nativeLanguage = context?.resources?.configuration?.locales?.get(0)?.language
            println("Native Language:$nativeLanguage")

            val idiomOptions = resources.getStringArray(R.array.language_values) // Posibilidades
            idiomOptions.forEach {
                Log.d("TEST", "$it $nativeLanguage")
            }
            val languageSelIndex = idiomOptions.indexOf(nativeLanguage)

            languagePref?.setValueIndex(languageSelIndex)
            languagePref?.setOnPreferenceChangeListener { _, newValue ->
                updateLanguage(newValue as String)
                true
            }

            val fontSizePref = findPreference<ListPreference>("font_size")
            fontSizePref?.setOnPreferenceChangeListener { _, newValue ->
                updateFontSize(newValue as String)
                true
            }

            val darkModePref = findPreference<SwitchPreferenceCompat>("dark_mode")
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            val darkMode = when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_YES -> true // Dark mode is active
                else -> false // Light mode or undefined
            }
            darkModePref?.isChecked =darkMode
            println("Dark Mode:$darkMode")
            darkModePref?.setOnPreferenceChangeListener { _, newValue ->
                updateDarkMode(newValue as Boolean)
                true
            }
        }

        private fun updateLanguage(languageCode: String) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)


            val configuration = Configuration(resources.configuration)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                configuration.setLocale(locale)  // API 26 y superior
            } else {
                configuration.locale = locale  // API menor a 26
            }

            // Cambiar el idioma a español, por ejemplo
            val contextoConIdioma = context?.createConfigurationContext(configuration)
            // Para usar el nuevo contexto
            val configuracionContextual = contextoConIdioma!!.resources.configuration
            activity?.recreate()

        }

        private fun updateFontSize(size: String) {
            val fontSize: Int
            when (size) {
                "small" -> fontSize = 14
                "medium" -> fontSize = 18
                "large" -> fontSize = 22
                else -> fontSize = 18 // Valor por defecto
            }

            // Actualizar el tamaño de fuente de las vistas
            val sharedPreferences = activity?.getSharedPreferences("app_preferences", MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.putInt("font_size", fontSize)
            editor?.apply()

            // Cambiar el tamaño de fuente de toda la actividad
            val currentActivity = activity as? AppCompatActivity
            currentActivity?.let {
                val configuration = Configuration(it.resources.configuration)
                configuration.fontScale = fontSize / 18f // 18 es el tamaño medio base
                it.resources.updateConfiguration(configuration, it.resources.displayMetrics)

                // Recargar la actividad para que el cambio tenga efecto
                it.recreate()
            }

        }

        private fun updateDarkMode(isDarkMode: Boolean) {
            val mode =
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
            activity?.recreate()
        }
    }
}