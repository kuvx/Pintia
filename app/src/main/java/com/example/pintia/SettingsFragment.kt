package com.example.pintia

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.pintia.components.Header
import com.example.pintia.utils.settings.DarkModeUtils
import com.example.pintia.utils.settings.FontSizeUtils
import com.example.pintia.utils.settings.LanguageUtils

class SettingsFragment : PreferenceFragmentCompat() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<Header>(R.id.header)
            .title = getString(R.string.settings)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
        setupLanguagePreference()
        setupFontSizePreference()
        setupDarkModePreference()
    }


    // Language

    private fun setupLanguagePreference() {
        val languagePref = findPreference<ListPreference>("language")
        val currentLanguage = LanguageUtils.getCurrentLanguage(requireContext())

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
        LanguageUtils.changeLanguage(requireContext(), languageCode)

        // Actualizar el fragmento sin reiniciar
        (requireActivity() as MainActivity).changeFragment(SettingsFragment())
    }

    // Font Size

    private fun setupFontSizePreference() {
        val fontSizePref = findPreference<ListPreference>("font_size")
        fontSizePref?.setOnPreferenceChangeListener { _, newValue ->
            FontSizeUtils.changeFontSize(requireActivity(), newValue.toString())
            true
        }
    }

    // Dark Mode

    private fun setupDarkModePreference() {
        val darkModePref = findPreference<SwitchPreferenceCompat>("dark_mode")

        // Asegurarnos de que la referencia de Configuration esté correctamente importada
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        darkModePref?.isChecked = (currentNightMode == Configuration.UI_MODE_NIGHT_YES)

        darkModePref?.setOnPreferenceChangeListener { _, newValue ->
            DarkModeUtils.updateDarkMode(requireContext(), newValue as Boolean)
            true
        }
    }
}
