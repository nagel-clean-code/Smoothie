package com.example.smoothie.domain.usecase.sharedpref

import com.example.smoothie.domain.repository.RecipeRepository

class GetNameRecipeFromSharPrefUseCase(private val recipeRepository: RecipeRepository) {
    fun execute(): String = recipeRepository.getNameRecipeFromSharPref()
}