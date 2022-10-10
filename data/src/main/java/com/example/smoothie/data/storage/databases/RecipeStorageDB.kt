package com.example.smoothie.data.storage.databases

import com.example.smoothie.domain.models.IRecipeModel

interface RecipeStorageDB {
    suspend fun saveRecipe(recipe: IRecipeModel)
    suspend fun nextRecipe(): IRecipeModel
    suspend fun saveImage(imageByteArray: ByteArray): String
    suspend fun getImageByUrl(url: String): ByteArray
    suspend fun getRecipes(searchBy: String, first: Int, last: Int): List<IRecipeModel>
    suspend fun saveFavoriteFlag(idRecipe: Int, flag: Boolean)
    suspend fun deleteRecipe(idRecipe: Int)
}