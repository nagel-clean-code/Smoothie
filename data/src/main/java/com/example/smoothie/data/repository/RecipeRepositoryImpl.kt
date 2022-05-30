package com.example.smoothie.data.repository

import com.example.smoothie.data.storage.databases.RecipeStorageDB
import com.example.smoothie.data.storage.sharedprefs.RecipeStorageSharPref
import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.domain.repository.RecipeRepository


class RecipeRepositoryImpl(
    private val recipeStorage: RecipeStorageDB,
    private val sharedPrefRecipeStorage: RecipeStorageSharPref
) : RecipeRepository {

    override fun saveRecipeDataBase(recipe: IRecipeModel) {
        recipeStorage.saveRecipe(recipe)
    }

    override suspend fun getRandomRecipe(): IRecipeModel = recipeStorage.nextRecipe()
    
    override suspend fun getImageFromLinkFromDB(url: String): ByteArray {
        return recipeStorage.getImageByUrl(url)
    }

    override suspend fun getListRecipe(page: Int, pageSize: Int): List<IRecipeModel> {
        return recipeStorage.getRecipes(page*pageSize,page*pageSize+pageSize)
    }

    override fun getLastRecipe() {
        TODO("Not yet implemented")
    }

    override fun getRecipeById(id: Long) {
        TODO("Not yet implemented")
    }

    override fun saveNameRecipeInSharPref(name: String) {
        sharedPrefRecipeStorage.saveNameRecipe(name)
    }

    override fun getIngredientsFromSharPref() = sharedPrefRecipeStorage.getIngredients()


    override fun saveIngredientsInSharPref(textIngredients: String) {
        sharedPrefRecipeStorage.saveIngredients(textIngredients)
    }

    override fun getNameRecipeFromSharPref() = sharedPrefRecipeStorage.getNameRecipe()

    override fun saveImageFromAddFormInSharPref(imageString: String) {
        sharedPrefRecipeStorage.saveImageFromAddForm(imageString)
    }

    override fun getImageFromAddFormFromSharPref() = sharedPrefRecipeStorage.getImageFromAddForm()

    override suspend fun saveImageFromAddFormToDb(imageByteArray: ByteArray): String {
        return recipeStorage.saveImage(imageByteArray)
    }
}