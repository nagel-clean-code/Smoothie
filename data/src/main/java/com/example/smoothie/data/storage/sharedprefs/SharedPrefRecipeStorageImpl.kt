package com.example.smoothie.data.storage.sharedprefs

import android.content.Context

class SharedPrefRecipeStorageImpl(context: Context) : RecipeStorageSharPref {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_ADD_RECIPE_NAME, Context.MODE_PRIVATE)

    override fun saveNameRecipe(name: String) {
        sharedPreferences.edit()
            .putString(NAME_RECIPE, name).apply()
    }

    override fun saveIngredients(ingredients: String) {
        sharedPreferences.edit().putString(INGREDIENTS, ingredients).apply()
    }

    override fun saveRecipe(recipe: String) {
        TODO("Not yet implemented")
    }

    override fun saveDescription(description: String) {
        TODO("Not yet implemented")
    }

    override fun saveUserName(userName: String) =
        sharedPreferences.edit().putString(USER_NAME, userName).apply()

    override fun saveImageFromAddForm(imageString: String) {
        sharedPreferences.edit().putString(IMAGE_FROM_ADD_FORM, imageString).apply()
    }

    override fun getNameRecipe() =
        sharedPreferences.getString(NAME_RECIPE, "Ошибка") ?: "sharedPreferences=null"

    override fun getImageFromAddForm() =
        sharedPreferences.getString(IMAGE_FROM_ADD_FORM, "") ?: ""

    override fun getIngredients() = sharedPreferences.getString(INGREDIENTS, "Ошибка") ?: ""

    override fun getUserName(): String = sharedPreferences.getString(USER_NAME, "no_name") ?: ""

    private companion object{
        const val SHARED_PREFS_ADD_RECIPE_NAME = "SHARED_PREFS_ADD_RECIPE_NAME"
        const val NAME_RECIPE = "NAME_RECIPE"
        const val INGREDIENTS = "INGREDIENTS"
        const val USER_NAME = "USER_NAME"
        const val IMAGE_FROM_ADD_FORM = "IMAGE_FROM_ADD_FORM"
    }
}