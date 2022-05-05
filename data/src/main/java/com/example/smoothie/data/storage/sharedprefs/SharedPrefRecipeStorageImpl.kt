package com.example.smoothie.data.storage.sharedprefs

import android.content.Context
import com.example.smoothie.data.storage.RecipeStorage
import com.example.smoothie.domain.models.Ingredients

private const val SHARED_PREFS_ADD_RECIPE_NAME = "SHARED_PREFS_ADD_RECIPE_NAME"
private const val NAME_RECIPE = "NAME_RECIPE"
private const val INGREDIENTS = "INGREDIENTS"

class SharedPrefRecipeStorageImpl(private val context: Context): RecipeStorage {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_ADD_RECIPE_NAME, Context.MODE_PRIVATE)

    override fun saveNameRecipe(name: String){
        sharedPreferences.edit()
            .putString(NAME_RECIPE, name).apply()
    }

    override fun getNameRecipe(): String {
        return sharedPreferences.getString(NAME_RECIPE, "Ошибка") ?: "sharedPreferences=null"
    }

    override fun saveIngredients(ingredients: Ingredients) {
        sharedPreferences.edit()
            .putString(INGREDIENTS, ingredients.toString()).apply()
    }

    override fun getIngredients(): Ingredients {
        return Ingredients(sharedPreferences.getString(INGREDIENTS, "Ошибка") ?: "")
    }

    override fun saveRecipe(recipe: String) {
        TODO("Not yet implemented")
    }

    override fun saveDescription(description: String) {
        TODO("Not yet implemented")
    }
}