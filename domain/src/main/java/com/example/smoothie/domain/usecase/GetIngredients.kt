package com.example.smoothie.domain.usecase

import com.example.smoothie.domain.models.Ingredients
import com.example.smoothie.domain.repository.RecipeRepository

class GetIngredients(private val recipeRepository: RecipeRepository) {
    fun execute(): Ingredients = recipeRepository.getIngredients()
}