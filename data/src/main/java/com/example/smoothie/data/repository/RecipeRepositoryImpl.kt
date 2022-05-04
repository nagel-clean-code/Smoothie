package com.example.smoothie.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.smoothie.domain.models.Ingredients
import com.example.smoothie.domain.repository.RecipeRepository

private const val SHARED_PREFS_ADD_RECIPE_NAME = "SHARED_PREFS_ADD_RECIPE_NAME"
private const val NAME_RECIPE = "NAME_RECIPE"

class RecipeRepositoryImpl(private val context: Context) : RecipeRepository {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_ADD_RECIPE_NAME, Context.MODE_PRIVATE)

    override fun saveLocalNameRecipe(name: String){
        sharedPreferences.edit()
            .putString(NAME_RECIPE, name).apply()
    }

    override fun getLocalNameRecipe(): String {
        return sharedPreferences.getString(NAME_RECIPE, "Ошибка") ?: ""
    }

    override fun saveLocalIngredients(ingredients: Ingredients) {
        TODO("Not yet implemented")
    }

    override fun saveLocalRecipe(recipe: String) {
        TODO("Not yet implemented")
    }

    override fun saveLocalDescription(description: String) {
        TODO("Not yet implemented")
    }

}