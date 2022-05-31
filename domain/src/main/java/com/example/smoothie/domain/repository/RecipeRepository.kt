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
    suspend fun saveImageFromAddFormToDb(imageByteArray: ByteArray): String
    suspend fun getRandomRecipe(): IRecipeModel
    suspend fun getImageFromLinkFromDB(url: String): ByteArray
    suspend fun getListRecipe(start: Int, count: Int): List<IRecipeModel>
    suspend fun saveFavoriteFlag(idRecipe: Int, flag: Boolean)
    suspend fun deleteRecipe(idRecipe: Int)
}