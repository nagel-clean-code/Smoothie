package com.example.smoothie.data.storage.sharedprefs

import android.content.Context

class SessionStorageSharPrefImpl(context: Context): SessionStorageSharPref {
    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_SESSION, Context.MODE_PRIVATE)

    override fun saveUserName(userName: String) =
        sharedPreferences.edit().putString(USER_NAME, userName).apply()

    override fun getUserName(): String = sharedPreferences.getString(USER_NAME, "no_name") ?: ""

    private companion object{
        const val USER_NAME = "USER_NAME"
        const val SHARED_PREFS_SESSION = "SHARED_PREFS_SESSION"
    }
}