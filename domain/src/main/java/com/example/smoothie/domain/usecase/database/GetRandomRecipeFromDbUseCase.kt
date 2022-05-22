package com.example.smoothie.domain.usecase.database

import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.domain.repository.RecipeRepository

class GetRandomRecipeFromDbUseCase(private val recipeRepository: RecipeRepository) {
    suspend fun execute(): IRecipeModel = recipeRepository.getRandomRecipe()
}