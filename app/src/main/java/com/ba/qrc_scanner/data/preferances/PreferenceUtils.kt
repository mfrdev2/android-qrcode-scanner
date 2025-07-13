package com.ba.qrc_scanner.data.preferances

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object PreferenceUtils {
    private const val PREFERENCES_FILE_KEY = "com.ba.qrc_scanner.PREFERENCE_FILE_KEY"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
    }

    // Example function to save a String
    fun saveString(context: Context, key: String, value: String) {
        getSharedPreferences(context).edit() {
            putString(key, value)
        }
    }

    // Example function to retrieve a String
    fun getString(context: Context, key: String, defaultValue: String? = null): String? {
        return getSharedPreferences(context)
            .getString(key, defaultValue)
    }
}