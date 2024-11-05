package com.example.pintia

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
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()


    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey)

            val languagePref = findPreference<ListPreference>("language")
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
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            activity?.recreate()
        }
    }
}