package com.example.pintia.utils.settings

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.forEach
import com.example.pintia.R

object FontSizeUtils {

    // Obtener el tamaño de fuente actual
    fun getCurrentFontSize(context: Context): Float {
        return context.resources.configuration.fontScale
    }

    // Obtener el tamaño de la fuente desde SharedPreferences
    private fun getSavedFontSize(context: Context): Float {
        return getFontSize(
            PreferenceUtils.getPreference(context, "font_size", BASE_FONT_KEY)
        )
    }

    // TODO extraer a values
    private const val BASE_FONT_KEY = "medium"
    private const val BASE_FONT = 18f

    private fun configFontSize(ac: Activity, fontSize: Float) {
        val configuration = Configuration(ac.resources.configuration)
        configuration.fontScale = fontSize / BASE_FONT
        ac.resources.updateConfiguration(configuration, ac.resources.displayMetrics)
    }

    fun changeFontSize(ac: Activity, size: String) {
        val fontSize = getFontSize(size)

        PreferenceUtils.savePreference(ac, "font_size", size)

        configFontSize(ac, fontSize)
        applyNewFontSize(ac.findViewById(R.id.main), fontSize)
    }

    private fun applyNewFontSize(views: ViewGroup, fontSize: Float) {
        views.forEach { child ->
            when (child) {
                is TextView -> child.textSize = fontSize
                is ViewGroup -> applyNewFontSize(child, fontSize)
            }
        }
    }

    private val fontSizes = mapOf(
        "small" to 14f,
        BASE_FONT_KEY to BASE_FONT,
        "large" to 22f
    )

    private fun getFontSize(size: String): Float {
        return fontSizes.getOrDefault(size, BASE_FONT)
    }

    fun applySavedFontSize(view: View, ac: Activity) {
        val context = view.context
        val fontSize = getSavedFontSize(context)

        configFontSize(ac, fontSize)
        applyNewFontSize((view as ViewGroup), fontSize)
    }
}
