package com.example.pintia

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.pintia.components.Header
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val mainActivity = requireActivity() as MainActivity

        // Configurar el título del encabezado
        val header = mainActivity.findViewById<Header>(R.id.header)
        header.title = getString(R.string.settings)

        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

        setupLanguagePreference()
        setupFontSizePreference()
        setupDarkModePreference()
    }

    private fun setupLanguagePreference() {
        val languagePref = findPreference<ListPreference>("language")
        val currentLanguage = Locale.getDefault().language
        Log.d("SettingsFragment", "Current Language: $currentLanguage")

        val languageOptions = resources.getStringArray(R.array.language_values)
        val currentLanguageIndex = languageOptions.indexOf(currentLanguage)

        if (currentLanguageIndex >= 0) {
            languagePref?.setValueIndex(currentLanguageIndex)
        }

        languagePref?.setOnPreferenceChangeListener { _, newValue ->
            updateLanguage(newValue as String)
            true
        }
    }

    private fun updateLanguage(languageCode: String) {
        try {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val config = Configuration(resources.configuration)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale)
                context?.createConfigurationContext(config)
            } else {
                config.locale = locale
            }

            resources.updateConfiguration(config, resources.displayMetrics)

            // Guardar la configuración de idioma para mantenerla en futuras sesiones
            val sharedPreferences = activity?.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            sharedPreferences?.edit()?.putString("language", languageCode)?.apply()

            // Reiniciar la actividad para aplicar el cambio de idioma
            activity?.recreate()
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error al cambiar de idioma: ${e.message}", e)
        }
    }

    private fun setupFontSizePreference() {
        val fontSizePref = findPreference<ListPreference>("font_size")
        fontSizePref?.setOnPreferenceChangeListener { _, newValue ->
            updateFontSize(newValue as String)
            true
        }
    }

    private fun updateFontSize(size: String) {
        val fontScale = when (size) {
            "small" -> 0.85f
            "medium" -> 1.0f
            "large" -> 1.15f
            else -> 1.0f
        }

        val config = Configuration(resources.configuration)
        config.fontScale = fontScale

        resources.updateConfiguration(config, resources.displayMetrics)

        // Guardar el tamaño de fuente en SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.putString("font_size", size)?.apply()

        activity?.recreate() // Recargar la actividad para aplicar los cambios
    }

    private fun setupDarkModePreference() {
        val darkModePref = findPreference<SwitchPreferenceCompat>("dark_mode")
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        darkModePref?.isChecked = (currentNightMode == Configuration.UI_MODE_NIGHT_YES)

        darkModePref?.setOnPreferenceChangeListener { _, newValue ->
            updateDarkMode(newValue as Boolean)
            true
        }
    }

    private fun updateDarkMode(isDarkMode: Boolean) {
        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
        activity?.recreate() // Recargar la actividad para aplicar los cambios
    }
}
