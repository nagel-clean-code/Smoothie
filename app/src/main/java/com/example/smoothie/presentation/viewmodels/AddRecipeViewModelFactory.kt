package com.example.smoothie.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smoothie.data.repository.RecipeRepositoryImpl
import com.example.smoothie.data.storage.sharedprefs.SharedPrefRecipeStorageImpl
import com.example.smoothie.domain.usecase.GetIngredients
import com.example.smoothie.domain.usecase.GetNameRecipe
import com.example.smoothie.domain.usecase.SaveIngredients
import com.example.smoothie.domain.usecase.SaveNameRecipe

class AddRecipeViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val recipeRepositoryImpl by lazy(LazyThreadSafetyMode.NONE) {
        RecipeRepositoryImpl(
            SharedPrefRecipeStorageImpl(context)
        )
    }
    private val saveIngredients by lazy(LazyThreadSafetyMode.NONE) {
        SaveIngredients(
            recipeRepositoryImpl
        )
    }
    private val getIngredients by lazy(LazyThreadSafetyMode.NONE) {
        GetIngredients(
            recipeRepositoryImpl
        )
    }

    private val saveNameRecipe by lazy(LazyThreadSafetyMode.NONE){
        SaveNameRecipe(
            recipeRepositoryImpl
        )
    }

    private val getNameRecipe by lazy(LazyThreadSafetyMode.NONE){
        GetNameRecipe(
            recipeRepositoryImpl
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddRecipeViewModel(saveIngredients, getIngredients,
        saveNameRecipe, getNameRecipe) as T
    }
}