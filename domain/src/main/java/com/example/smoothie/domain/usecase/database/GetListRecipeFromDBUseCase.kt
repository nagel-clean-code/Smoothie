package com.example.smoothie.domain.usecase.database

import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.domain.repository.RecipeRepository

class GetListRecipeFromDBUseCase(private val recipeRepository: RecipeRepository) {
    suspend fun execute(searchBy: String, start: Int, count: Int): List<IRecipeModel> =
        recipeRepository.getListRecipe(searchBy, start, count)
}