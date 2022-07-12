package com.example.smoothie.domain.usecase.sharedpref

import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.domain.repository.RecipeRepository

class GetRecipeFromSharPrefUseCase(private val recipeRepository: RecipeRepository) {
    fun execute(key: String): IRecipeModel? = recipeRepository.getRecipeFromSharPref(key)
}