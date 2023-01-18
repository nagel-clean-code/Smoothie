package com.example.smoothie.data.storage.databases.external

interface SessionStorageFB {
    suspend fun createNewUserAccount(): String
}