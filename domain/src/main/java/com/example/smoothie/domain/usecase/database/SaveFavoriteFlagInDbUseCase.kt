package com.example.smoothie.domain.usecase.database

import com.example.smoothie.domain.repository.RecipeRepository

class SaveFavoriteFlagInDbUseCase(private val recipeRepository: RecipeRepository) {
    suspend fun execute(idRecipe: Int, flag: Boolean) =
        recipeRepository.saveFavoriteFlag(idRecipe, flag)
}