package com.akobusinska.letsplay.utils

import android.app.Activity
import android.content.Context
import com.akobusinska.letsplay.utils.Keys.SHARED_PREFERENCES

class Storage {

    fun saveStringData(context: Context, key: String?, text: String?) {
        val preferencesEditor =
            context.getSharedPreferences(SHARED_PREFERENCES.key, Activity.MODE_PRIVATE).edit()
        preferencesEditor.putString(key, text)
        preferencesEditor.apply()
    }

    fun restoreStringData(context: Context, key: String?): String? {
        return context.getSharedPreferences(SHARED_PREFERENCES.key, Activity.MODE_PRIVATE)
            .getString(key, null)
    }

    fun saveIntData(context: Context, key: String?, text: Int) {
        val preferencesEditor =
            context.getSharedPreferences(SHARED_PREFERENCES.key, Activity.MODE_PRIVATE).edit()
        preferencesEditor.putInt(key, text)
        preferencesEditor.apply()
    }

    fun restoreIntData(context: Context, key: String?): Int {
        return context.getSharedPreferences(SHARED_PREFERENCES.key, Activity.MODE_PRIVATE)
            .getInt(key, 0)
    }
}