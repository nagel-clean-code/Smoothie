package com.example.smoothie.domain.usecase.sharedpref.recipe

import com.example.smoothie.domain.repository.RecipeRepository

class GetCustomCategoriesListFromSharPrefsUseCase(private val recipeRepository: RecipeRepository) {
    fun execute(key: String? = null): MutableList<String>? = recipeRepository.getCustomCategoriesListInSharPrefs(key)
}