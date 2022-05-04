package com.example.smoothie.domain.repository

import com.example.smoothie.domain.models.Ingredients

interface RecipeRepository {

    fun saveLocalNameRecipe(name: String)
    fun getLocalNameRecipe(): String
    fun saveLocalIngredients(ingredients: Ingredients)

    fun saveLocalRecipe(recipe: String)

    fun saveLocalDescription(description: String)
}