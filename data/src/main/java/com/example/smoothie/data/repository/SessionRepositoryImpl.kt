package com.example.smoothie.data.repository

import com.example.smoothie.data.storage.databases.external.SessionStorageFB
import com.example.smoothie.data.storage.sharedprefs.SessionStorageSharPref
import com.example.smoothie.domain.repository.SessionRepository

class SessionRepositoryImpl(
    private val sessionStorageSharPref: SessionStorageSharPref,
    private val sessionStorageDb: SessionStorageFB
) : SessionRepository {
    override fun getUserNameFromSharPref(): String = sessionStorageSharPref.getUserName()
    override fun saveUserNameInSharPref(userName: String) = sessionStorageSharPref.saveUserName(userName)
    override suspend fun createNewUserAccountInDb(): String = sessionStorageDb.createNewUserAccount()

}