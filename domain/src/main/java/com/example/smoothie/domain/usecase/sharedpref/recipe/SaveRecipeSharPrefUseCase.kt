package com.example.smoothie.domain.usecase.sharedpref.recipe

import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.domain.repository.RecipeRepository

class SaveRecipeSharPrefUseCase(private val recipeRepository: RecipeRepository) {
    fun execute(recipe: IRecipeModel, kay: String){
        recipeRepository.saveRecipeInSharPref(recipe, kay)
    }
}