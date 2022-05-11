package com.example.smoothie.data.storage.sharedprefs

import com.example.smoothie.domain.models.Ingredients

interface RecipeStorageSharPref {
    fun saveNameRecipe(name: String)
    fun getNameRecipe(): String
    fun saveIngredients(ingredients: Ingredients)
    fun getIngredients(): Ingredients
    fun saveRecipe(recipe: String)
    fun saveDescription(description: String)
}