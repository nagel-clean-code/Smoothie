package com.example.smoothie.domain.usecase.sharedpref.sesion

import com.example.smoothie.domain.repository.SessionRepository

class GetUserNameFromSharPrefUseCase (private val repository: SessionRepository) {
    fun execute(): String = repository.getUserNameFromSharPref()
}