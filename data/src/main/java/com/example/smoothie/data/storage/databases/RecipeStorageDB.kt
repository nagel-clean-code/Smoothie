package com.example.smoothie.data.storage.databases

import com.example.smoothie.domain.models.IRecipeModel

interface RecipeStorageDB {
    fun saveRecipe(recipe: IRecipeModel)
    suspend fun nextRecipe(): IRecipeModel
    suspend fun saveImage(imageByteArray: ByteArray): String
    suspend fun getImageByUrl(url: String): ByteArray
}