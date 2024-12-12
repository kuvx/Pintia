package com.example.pintia.utils.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object DarkModeUtils {

    // Guardar el estado del modo oscuro en SharedPreferences
    private fun saveDarkModeState(context: Context, darkMode: Boolean) {
        PreferenceUtils.savePreference(context, "dark_mode", darkMode)
    }

    fun updateDarkMode(context: Context, isDarkMode: Boolean) {
        // Cambiar el modo oscuro sin reiniciar
        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        saveDarkModeState(context, isDarkMode)
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
