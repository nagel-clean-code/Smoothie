package com.example.smoothie.domain.usecase.database

import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.domain.repository.RecipeRepository

class SaveRecipeToDbUseCase(private val recipeRepository: RecipeRepository) {
    suspend fun execute(recipe: IRecipeModel) {
        recipeRepository.saveRecipeDataBase(recipe)
    }
}