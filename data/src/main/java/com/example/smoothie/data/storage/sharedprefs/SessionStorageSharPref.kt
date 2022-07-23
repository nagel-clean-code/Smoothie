package com.example.smoothie.data.storage.sharedprefs

interface SessionStorageSharPref {
    fun getUserName(): String
    fun saveUserName(userName: String)
}