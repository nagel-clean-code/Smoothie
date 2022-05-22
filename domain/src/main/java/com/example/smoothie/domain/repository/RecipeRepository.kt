package com.example.smoothie.domain.repository

import com.example.smoothie.domain.models.IRecipeModel

interface RecipeRepository {
    fun saveRecipeDataBase(recipe: IRecipeModel)
    fun getLastRecipe()
    fun getRecipeById(id: Long)
    fun saveNameRecipeInSharPref(name: String)
    fun getIngredientsFromSharPref(): String
    fun saveIngredientsInSharPref(textIngredients: String)
    fun getNameRecipeFromSharPref(): String
    fun saveImageFromAddFormInSharPref(imageString: String)
    fun getImageFromAddFormFromSharPref(): String
    suspend fun saveImageFromAddFormToDb(imagePatch: String): String
    suspend fun getRandomRecipe(): IRecipeModel
}