package com.example.smoothie.di

import com.example.smoothie.domain.usecase.GetIngredients
import com.example.smoothie.domain.usecase.GetNameRecipe
import com.example.smoothie.domain.usecase.SaveIngredients
import com.example.smoothie.domain.usecase.SaveNameRecipe
import org.koin.dsl.module

val domainModule = module{

    factory<SaveIngredients>{
        SaveIngredients(recipeRepository = get())
    }

    factory<GetIngredients>{
        GetIngredients(recipeRepository = get())
    }

    factory<SaveNameRecipe>{
        SaveNameRecipe(recipeRepository = get())
    }

    factory<GetNameRecipe>{
        GetNameRecipe(recipeRepository = get())
    }
}