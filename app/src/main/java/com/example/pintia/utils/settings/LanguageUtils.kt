package com.example.pintia.utils.settings

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.view.View
import java.util.*

object LanguageUtils {

    private const val LANGUAGE_KEY = "language"

    // Cambia el idioma a partir del código de idioma proporcionado
    fun changeLanguage(context: Context, languageCode: String) {
        try {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val configuration = Configuration(context.resources.configuration)
            configuration.setLocale(locale)

            // Aplica la nueva configuración
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)

            // Guardamos la configuración
            PreferenceUtils.savePreference(context, LANGUAGE_KEY, languageCode)
        } catch (e: Exception) {
            Log.e("LanguageUtils", "Error changing language", e)
        }
    }

    fun getCurrentLanguage(context: Context): String {
        val savedLanguage = PreferenceUtils.getPreference(context, LANGUAGE_KEY, "")
        return savedLanguage.ifBlank { Locale.getDefault().language }
    }

    fun applySavedLanguage(view: View) {
        val context = view.context
        changeLanguage(context, getCurrentLanguage(context))
    }
}
