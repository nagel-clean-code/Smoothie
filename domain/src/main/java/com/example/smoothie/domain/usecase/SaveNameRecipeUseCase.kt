package com.example.smoothie.domain.usecase

import com.example.smoothie.domain.models.Ingredients
import com.example.smoothie.domain.repository.RecipeRepository

class SaveNameRecipeUseCase (private val recipeRepository: RecipeRepository) {
    fun execute(text: String){
        recipeRepository.saveNameRecipe(text)
    }
}