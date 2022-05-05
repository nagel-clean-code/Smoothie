package com.example.smoothie.domain.repository

import com.example.smoothie.domain.models.Ingredients

interface RecipeRepository {

    fun saveNameRecipe(name: String)
    fun getNameRecipe(): String

    fun saveIngredients(ingredients: Ingredients)
    fun getIngredients(): Ingredients

    fun saveRecipe(recipe: String)

    fun saveDescription(description: String)
}