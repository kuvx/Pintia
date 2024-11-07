package com.example.pintia

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.components.Header
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import java.util.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.settings)
        val settings = SettingsFragment()
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
            languagePref?.setValueIndex(arrayOf("es", "en").indexOf(nativeLanguage))
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
            // Implementa la lógica para cambiar el tamaño de la fuente
            // Puedes usar un tema personalizado o ajustar el tamaño de texto en tu app
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