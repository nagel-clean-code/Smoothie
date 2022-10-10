package com.example.smoothie.data.storage.sharedprefs

import android.content.Context

class SessionStorageSharPrefImpl(context: Context) : SessionStorageSharPref {
    private var userNameBuf: String? = null
    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_SESSION, Context.MODE_PRIVATE)

    override fun saveUserName(userName: String) {
        sharedPreferences.edit().putString(USER_NAME, userName).apply()
        userNameBuf = userName
    }

    override fun getUserName(): String {
        if (userNameBuf == null) {
            userNameBuf = sharedPreferences.getString(USER_NAME, "no_name") ?: ""
        }
        return userNameBuf!!
    }

    private companion object {
        const val USER_NAME = "USER_NAME"
        const val SHARED_PREFS_SESSION = "SHARED_PREFS_SESSION"
    }
}