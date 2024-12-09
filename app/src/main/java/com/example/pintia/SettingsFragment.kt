package com.example.pintia

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.pintia.components.Header
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {

 

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getMain().findViewById<Header>(R.id.header)
            .title = getString(R.string.settings)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
        setupLanguagePreference()
        setupFontSizePreference()
        setupDarkModePreference()
    }

    private fun initializeHeader() {
        // Inicialización del Header, asegurándonos de que esté correctamente importado
        header = requireActivity().findViewById(R.id.header)
        header?.apply {
            title = getString(R.string.settings)
        }
    }

    private fun setupLanguagePreference() {
        val languagePref = findPreference<ListPreference>("language")
        val currentLanguage = Locale.getDefault().language

        val idiomOptions = resources.getStringArray(R.array.language_values)
        val languageSelIndex = idiomOptions.indexOf(nativeLanguage)
        
        // Suponemos que el indice es uno valido
        languagePref?.setOnPreferenceChangeListener { _, newValue ->
            updateLanguage(newValue as String)
            true
        }
    }

    private fun updateLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        // Actualizamos la configuración de recursos
        resources.updateConfiguration(config, resources.displayMetrics)

        // Guardar el idioma seleccionado en SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.putString("language", languageCode)?.apply()

        // Actualizar el fragmento sin reiniciar
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, this)  // Reemplazamos el fragmento en lugar de detach/attach
            .commit()

        header?.title = getString(R.string.settings)
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

        // Guardar el tamaño de fuente en SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.putString("font_size", size)?.apply()

        // Actualizar dinámicamente las vistas visibles
        updateVisibleTextViewsFontScale(fontScale)
    }

    private fun updateVisibleTextViewsFontScale(fontScale: Float) {
        // Recorrer las vistas visibles del fragmento
        view?.apply {
            (this as? ViewGroup)?.children?.forEach { child ->
                if (child is TextView) {
                    child.textSize = child.textSize * fontScale
                }
            }
        }
    }

    private fun setupDarkModePreference() {
        val darkModePref = findPreference<SwitchPreferenceCompat>("dark_mode")

        // Asegurarnos de que la referencia de Configuration esté correctamente importada
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        darkModePref?.isChecked = (currentNightMode == Configuration.UI_MODE_NIGHT_YES)

        darkModePref?.setOnPreferenceChangeListener { _, newValue ->
            updateDarkMode(newValue as Boolean)
            true
        }
    }

    private fun updateDarkMode(isDarkMode: Boolean) {
        // Cambiar el modo oscuro sin reiniciar
        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)

        // Actualizar las vistas visibles del fragmento
        updateBackgroundForDarkMode(isDarkMode)

        // Forzar la actualización del fragmento
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, this)  // Reemplazamos el fragmento en lugar de detach/attach
            .commit()
    }

    private fun updateBackgroundForDarkMode(isDarkMode: Boolean) {
        view?.apply {
            setBackgroundColor(
                if (isDarkMode) {
                    requireContext().getColor(android.R.color.background_dark) // Fondo oscuro
                } else {
                    requireContext().getColor(android.R.color.background_light) // Fondo claro
                }
            )

            // Aplicar colores a los TextView
            (this as? ViewGroup)?.children?.forEach { child ->
                if (child is TextView) {
                    child.setTextColor(
                        if (isDarkMode) {
                            requireContext().getColor(android.R.color.white)
                        } else {
                            requireContext().getColor(android.R.color.black)
                        }
                    )
                }
            }
        }
    }
}
