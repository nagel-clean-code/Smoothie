package com.example.smoothie.data.storage.sharedprefs

import com.example.smoothie.domain.models.IRecipeModel

interface RecipeStorageSharPref {
    fun saveRecipe(recipe: IRecipeModel, key: String)
    fun getRecipe(key: String): IRecipeModel?
    fun saveImageFromAddForm(imageString: String)
    fun getImageFromAddForm(): String
    fun saveCustomCategories(list: List<String>, key: String?)
    fun getCustomCategories(key: String?): MutableList<String>?
}