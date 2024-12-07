package com.akobusinska.letsplay.utils

import android.app.Activity
import android.content.Context
import com.akobusinska.letsplay.utils.Keys.CURRENT_USER_ID
import com.akobusinska.letsplay.utils.Keys.CURRENT_USER_NAME
import com.akobusinska.letsplay.utils.Keys.SHARED_PREFERENCES

class Storage {

    fun saveCurrentUserName(context: Context, text: String?) {
        val preferencesEditor =
            context.getSharedPreferences(SHARED_PREFERENCES.key, Activity.MODE_PRIVATE).edit()
        preferencesEditor.putString(CURRENT_USER_NAME.key, text)
        preferencesEditor.apply()
    }

    fun restoreCurrentUserName(context: Context): String {
        return context.getSharedPreferences(SHARED_PREFERENCES.key, Activity.MODE_PRIVATE)
            .getString(CURRENT_USER_NAME.key, "Default") ?: "Default"
    }

    fun saveCurrentUserId(context: Context, text: Long) {
        val preferencesEditor =
            context.getSharedPreferences(SHARED_PREFERENCES.key, Activity.MODE_PRIVATE).edit()
        preferencesEditor.putLong(CURRENT_USER_ID.key, text)
        preferencesEditor.apply()
    }

    fun restoreCurrentUserId(context: Context): Long {
        return context.getSharedPreferences(SHARED_PREFERENCES.key, Activity.MODE_PRIVATE)
            .getLong(CURRENT_USER_ID.key, 1L)
    }
}