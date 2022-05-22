package com.example.smoothie.data.storage.sharedprefs

interface RecipeStorageSharPref {
    fun saveNameRecipe(name: String)
    fun getNameRecipe(): String
    fun saveIngredients(ingredients: String)
    fun getIngredients(): String
    fun saveRecipe(recipe: String)
    fun saveDescription(description: String)
    fun getUserName(): String
    fun saveUserName(userName: String)
    fun saveImageFromAddForm(imageString: String)
    fun getImageFromAddForm(): String
}