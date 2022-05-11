package com.example.smoothie.domain.repository

import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.domain.models.Ingredients

interface RecipeRepository {
    fun saveRecipeDataBase(recipe: IRecipeModel)
    fun getLastRecipe()
    fun getRecipeById(id: Long)
    fun saveNameRecipe(name: String)
    fun getIngredients(): Ingredients
    fun saveIngredients(ingredients: Ingredients)
    fun getNameRecipe(): String
}