package com.example.smoothie.domain.usecase.database

import com.example.smoothie.domain.repository.RecipeRepository

class GetImageFromDBUseCase(private val recipeRepository: RecipeRepository) {
    suspend fun execute(url: String): ByteArray = recipeRepository.getImageFromLinkFromDB(url)
}