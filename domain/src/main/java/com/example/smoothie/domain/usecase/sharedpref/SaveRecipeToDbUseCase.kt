package com.example.smoothie.domain.usecase.sharedpref

import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.domain.repository.RecipeRepository

class SaveRecipeToDbUseCase(private val recipeRepository: RecipeRepository) {
    fun execute(recipe: IRecipeModel) {
        recipeRepository.saveRecipeDataBase(recipe)
    }
}