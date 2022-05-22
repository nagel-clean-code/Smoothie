package com.example.smoothie.domain.usecase.sharedpref

import com.example.smoothie.domain.repository.RecipeRepository

class SaveImageFromAddFormSharPrefUseCase(private val recipeRepository: RecipeRepository) {
    fun execute(imageString: String){
        recipeRepository.saveImageFromAddFormInSharPref(imageString)
    }
}