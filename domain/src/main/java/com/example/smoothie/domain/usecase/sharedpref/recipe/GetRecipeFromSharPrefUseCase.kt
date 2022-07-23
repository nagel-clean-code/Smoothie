package com.example.smoothie.domain.usecase.sharedpref.recipe

import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.domain.repository.RecipeRepository

class GetRecipeFromSharPrefUseCase(private val recipeRepository: RecipeRepository) {
    fun execute(kay: String): IRecipeModel? = recipeRepository.getRecipeFromSharPref(kay)
}