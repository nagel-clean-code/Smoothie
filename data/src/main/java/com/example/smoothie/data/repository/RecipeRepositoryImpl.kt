package com.example.smoothie.data.repository

import com.example.smoothie.data.storage.databases.RecipeStorageDB
import com.example.smoothie.data.storage.sharedprefs.RecipeStorageSharPref
import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.domain.repository.RecipeRepository


class RecipeRepositoryImpl(
    private val recipeStorage: RecipeStorageDB,
    private val sharedPrefRecipeStorage: RecipeStorageSharPref
) : RecipeRepository {

    override suspend fun saveRecipeDataBase(recipe: IRecipeModel) {
        recipeStorage.saveRecipe(recipe)
    }

    override suspend fun getRandomRecipe(): IRecipeModel = recipeStorage.nextRecipe()

    override suspend fun getImageFromLinkFromDB(url: String): ByteArray {
        return recipeStorage.getImageByUrl(url)
    }

    override suspend fun getListRecipe(
        searchBy: String,
        start: Int,
        count: Int
    ): List<IRecipeModel> {
        return recipeStorage.getRecipes(searchBy, start, start + count - 1)
    }

    override suspend fun saveFavoriteFlag(idRecipe: Int, flag: Boolean) = recipeStorage.saveFavoriteFlag(idRecipe, flag)

    override suspend fun deleteRecipe(idRecipe: Int) {
        recipeStorage.deleteRecipe(idRecipe)
    }

    override fun saveRecipeInSharPref(recipe: IRecipeModel, key: String) =
        sharedPrefRecipeStorage.saveRecipe(recipe, key)

    override fun getRecipeFromSharPref(key: String): IRecipeModel? = sharedPrefRecipeStorage.getRecipe(key)

    override fun saveImageFromAddFormInSharPref(imageString: String) =
        sharedPrefRecipeStorage.saveImageFromAddForm(imageString)

    override fun saveCustomCategoriesListInSharPrefs(categories: List<String>, key: String?) =
        sharedPrefRecipeStorage.saveCustomCategories(categories, key)

    override fun getCustomCategoriesListInSharPrefs(key: String?): MutableList<String>? =
        sharedPrefRecipeStorage.getCustomCategories(key)


    override fun getImageFromAddFormFromSharPref() = sharedPrefRecipeStorage.getImageFromAddForm()

    override suspend fun saveImageFromAddFormToDb(imageByteArray: ByteArray): String =
        recipeStorage.saveImage(imageByteArray)
}