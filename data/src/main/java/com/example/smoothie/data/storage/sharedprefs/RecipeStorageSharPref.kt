package com.example.smoothie.data.storage.sharedprefs

import com.example.smoothie.domain.models.IRecipeModel

interface RecipeStorageSharPref {
    fun saveNameRecipe(name: String)
    fun getNameRecipe(): String
    fun saveIngredients(ingredients: String)
    fun getIngredients(): String
    fun saveRecipe(recipe: IRecipeModel, key: String)
    fun saveDescription(description: String)
    fun getUserName(): String
    fun saveUserName(userName: String)
    fun saveImageFromAddForm(imageString: String)
    fun getImageFromAddForm(): String
    fun getRecipe(key: String): IRecipeModel?
}