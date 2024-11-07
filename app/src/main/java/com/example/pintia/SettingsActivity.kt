package com.example.pintia

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.components.Header
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import java.util.*
import android.content.res.Configuration

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
            println("Nati:$nativeLanguage")
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
            darkModePref?.setDefaultValue(AppCompatDelegate.getDefaultNightMode())
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