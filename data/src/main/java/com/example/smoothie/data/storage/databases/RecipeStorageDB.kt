package com.example.smoothie.data.storage.databases

import com.example.smoothie.domain.models.IRecipeModel

interface RecipeStorageDB {
    fun saveRecipe(recipe: IRecipeModel)
    suspend fun nextRecipe(): IRecipeModel
    suspend fun saveImage(imagePatch: String): String
}