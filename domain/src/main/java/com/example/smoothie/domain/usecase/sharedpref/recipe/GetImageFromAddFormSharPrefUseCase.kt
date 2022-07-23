package com.example.smoothie.domain.usecase.sharedpref.recipe

import com.example.smoothie.domain.repository.RecipeRepository

class GetImageFromAddFormSharPrefUseCase(private val repository: RecipeRepository) {
    fun execute(): String = repository.getImageFromAddFormFromSharPref()
}