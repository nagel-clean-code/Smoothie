package com.example.smoothie.di

import com.example.smoothie.data.repository.RecipeRepositoryImpl
import com.example.smoothie.data.storage.RecipeStorage
import com.example.smoothie.data.storage.sharedprefs.SharedPrefRecipeStorageImpl
import com.example.smoothie.domain.repository.RecipeRepository
import org.koin.dsl.module

val dataModule = module {
    single<RecipeStorage>{
        SharedPrefRecipeStorageImpl(context = get())
    }
    single<RecipeRepository>{
        RecipeRepositoryImpl(recipe = get())
    }
}