package com.example.smoothie.domain.usecase

import com.example.smoothie.domain.models.Ingredients
import com.example.smoothie.domain.repository.RecipeRepository

class GetNameRecipeUseCase(private val recipeRepository: RecipeRepository) {
    fun execute(): String = recipeRepository.getNameRecipe()
}