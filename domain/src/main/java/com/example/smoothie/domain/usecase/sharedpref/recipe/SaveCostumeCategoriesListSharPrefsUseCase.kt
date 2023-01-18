package com.example.smoothie.domain.usecase.sharedpref.recipe

import com.example.smoothie.domain.repository.RecipeRepository

class SaveCostumeCategoriesListSharPrefsUseCase(private val recipeRepository: RecipeRepository) {
    fun execute(category: List<String>, key: String? = null) {
        recipeRepository.saveCustomCategoriesListInSharPrefs(category, key)
    }
}