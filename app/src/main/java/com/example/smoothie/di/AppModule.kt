package com.example.smoothie.di

import com.example.smoothie.presentation.viewmodels.AddRecipeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel<AddRecipeViewModel> {
        AddRecipeViewModel(
            saveIngredients = get(),
            getIngredients = get(),
            saveNameRecipe = get(),
            getNameRecipe = get()
        )
    }
}