package com.example.smoothie.domain.usecase.sharedpref

import com.example.smoothie.domain.repository.RecipeRepository

class SaveIngredientsInSharPrefUseCase(private val recipeRepository: RecipeRepository) {
    fun execute(text: String){
        recipeRepository.saveIngredientsInSharPref(text)
    }
}