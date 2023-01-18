package com.example.smoothie.domain.usecase.database

import com.example.smoothie.domain.repository.SessionRepository

class CreateNewAccountDbUseCase(private val sessionRepository: SessionRepository) {
    suspend fun execute() = sessionRepository.createNewUserAccountInDb()
}