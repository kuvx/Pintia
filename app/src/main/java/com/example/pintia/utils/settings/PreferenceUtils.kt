package com.example.pintia.utils.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object PreferenceUtils {

    // Obtener una preferencia como String
    fun getPreference(context: Context, key: String, defaultValue: String): String {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    // Guardar una preferencia como String
    fun savePreference(context: Context, key: String, value: String) {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    // Obtener una preferencia como Boolean
    fun getPreference(context: Context, key: String, defaultValue: Boolean): Boolean {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    // Guardar una preferencia como Boolean
    fun savePreference(context: Context, key: String, value: Boolean) {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    // Obtener una preferencia como Float
    fun getPreference(context: Context, key: String, defaultValue: Float): Float {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getFloat(key, defaultValue)
    }

    // Guardar una preferencia como Float
    fun savePreference(context: Context, key: String, value: Float) {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putFloat(key, value)
        editor.apply()
    }
}
