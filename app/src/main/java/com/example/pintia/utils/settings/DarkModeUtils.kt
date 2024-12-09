package com.example.pintia.utils.settings

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.children

object DarkModeUtils {

    // Cambiar el modo oscuro/claro
    fun changeDarkMode(context: Context, darkMode: Boolean) {
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    // Obtener el estado actual del modo oscuro
    fun isDarkModeEnabled(): Boolean {
        return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    }

    // Guardar el estado del modo oscuro en SharedPreferences
    fun saveDarkModeState(context: Context, darkMode: Boolean) {
        PreferenceUtils.savePreference(context, "dark_mode", darkMode)
    }

    // Obtener el estado del modo oscuro desde SharedPreferences
    fun getSavedDarkModeState(context: Context): Boolean {
        return PreferenceUtils.getPreference(context, "dark_mode", false) // false es el valor por defecto
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
