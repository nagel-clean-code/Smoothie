package com.example.smoothie.data.storage

import com.example.smoothie.domain.models.Ingredients

interface RecipeStorage {
    fun saveNameRecipe(name: String)
    fun getNameRecipe(): String
    fun saveIngredients(ingredients: Ingredients)
    fun getIngredients(): Ingredients
    fun saveRecipe(recipe: String)
    fun saveDescription(description: String)
}