package com.example.smoothie.domain.usecase.database

import com.example.smoothie.domain.repository.RecipeRepository

class SaveImageInDBUseCase(private val recipeRepository: RecipeRepository) {
    suspend fun execute(imageByteArray: ByteArray): String =
         recipeRepository.saveImageFromAddFormToDb(imageByteArray)
}