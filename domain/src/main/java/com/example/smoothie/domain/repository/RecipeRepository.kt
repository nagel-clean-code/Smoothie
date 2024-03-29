package com.example.smoothie.domain.repository

import com.example.smoothie.domain.models.IRecipeModel

interface RecipeRepository {
    suspend fun saveRecipeDataBase(recipe: IRecipeModel)
    fun saveRecipeInSharPref(recipe: IRecipeModel, key: String)
    fun getRecipeFromSharPref(key: String): IRecipeModel?
    fun saveImageFromAddFormInSharPref(imageString: String)
    fun saveCustomCategoriesListInSharPrefs(categories: List<String>, key: String?)
    fun getCustomCategoriesListInSharPrefs(key: String?): MutableList<String>?
    fun getImageFromAddFormFromSharPref(): String
    suspend fun saveImageFromAddFormToDb(imageByteArray: ByteArray): String
    suspend fun getRandomRecipe(): IRecipeModel
    suspend fun getImageFromLinkFromDB(url: String): ByteArray
    suspend fun getListRecipe(searchBy: String, start: Int, count: Int): List<IRecipeModel>
    suspend fun saveFavoriteFlag(idRecipe: Int, flag: Boolean)
    suspend fun deleteRecipe(idRecipe: Int)
}