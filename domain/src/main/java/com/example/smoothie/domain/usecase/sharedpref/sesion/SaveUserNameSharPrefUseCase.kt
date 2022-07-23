package com.example.smoothie.domain.usecase.sharedpref.sesion

import com.example.smoothie.domain.repository.SessionRepository

class SaveUserNameSharPrefUseCase(private val recipeRepository: SessionRepository) {
    fun execute(userName: String) {
        recipeRepository.saveUserNameInSharPref(userName)
    }
}