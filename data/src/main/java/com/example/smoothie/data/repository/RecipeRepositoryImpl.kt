package com.example.smoothie.data.repository

import com.example.smoothie.data.storage.databases.RecipeStorageDB
import com.example.smoothie.data.storage.sharedprefs.RecipeStorageSharPref
import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.domain.models.Ingredients
import com.example.smoothie.domain.repository.RecipeRepository


class RecipeRepositoryImpl(
    private val recipeStorage: RecipeStorageDB,
    private val sharedPrefRecipeStorage: RecipeStorageSharPref
) : RecipeRepository {

    override fun saveRecipeDataBase(recipe: IRecipeModel) {
        recipeStorage.saveRecipe(recipe)
    }

    override fun getLastRecipe() {
        TODO("Not yet implemented")
    }

    override fun getRecipeById(id: Long) {
        TODO("Not yet implemented")
    }

    override fun saveNameRecipe(name: String) {
        sharedPrefRecipeStorage.saveNameRecipe(name)
    }

    override fun getIngredients(): Ingredients {
        return sharedPrefRecipeStorage.getIngredients()
    }

    override fun saveIngredients(ingredients: Ingredients) {
        sharedPrefRecipeStorage.saveIngredients(ingredients)
    }

    override fun getNameRecipe(): String {
        return sharedPrefRecipeStorage.getNameRecipe()
    }
}