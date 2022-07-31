package com.example.smoothie.domain.repository

interface SessionRepository {
    fun getUserNameFromSharPref(): String
    fun saveUserNameInSharPref(userName: String)
    suspend fun createNewUserAccountInDb(): String
}