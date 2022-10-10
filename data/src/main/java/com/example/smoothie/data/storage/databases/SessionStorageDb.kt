package com.example.smoothie.data.storage.databases

interface SessionStorageDb {
    suspend fun createNewUserAccount(): String
}