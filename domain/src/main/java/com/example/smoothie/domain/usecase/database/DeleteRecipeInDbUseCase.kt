package com.example.smoothie.domain.usecase.database

import com.example.smoothie.domain.repository.RecipeRepository

class DeleteRecipeInDbUseCase(private val recipeRepository: RecipeRepository) {
    suspend fun execute(idRecipe: Int) = recipeRepository.deleteRecipe(idRecipe)
}