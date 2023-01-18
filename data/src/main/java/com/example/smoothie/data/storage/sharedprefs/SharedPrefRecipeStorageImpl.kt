package com.example.smoothie.data.storage.sharedprefs

import android.content.Context
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.domain.models.IRecipeModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class SharedPrefRecipeStorageImpl(context: Context) : RecipeStorageSharPref {
    private var gson = Gson()
    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_RECIPE, Context.MODE_PRIVATE)

    override fun saveRecipe(recipe: IRecipeModel, key: String) {
        val json = gson.toJson(recipe)
        sharedPreferences.edit().putString(key, json).apply()
    }

    override fun saveImageFromAddForm(imageString: String) {
        sharedPreferences.edit().putString(IMAGE_FROM_ADD_FORM, imageString).apply()
    }

    override fun getImageFromAddForm() =
        sharedPreferences.getString(IMAGE_FROM_ADD_FORM, "") ?: ""

    override fun saveCustomCategories(list: List<String>, key: String?) {
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(key ?: CUSTOM_LIST_CATEGORIES, json).apply()
    }

    override fun getCustomCategories(key: String?): MutableList<String>? {
        val list = sharedPreferences.getString(key ?: CUSTOM_LIST_CATEGORIES, "") ?: return null
        val listType: Type = object : TypeToken<List<String?>?>() {}.type
        return gson.fromJson(list, listType)
    }

    override fun getRecipe(key: String): RecipeEntity? {
        val recipeString = sharedPreferences.getString(key, "") ?: return null
        return gson.fromJson(recipeString, RecipeEntity::class.java)
    }

    private companion object {
        const val CUSTOM_LIST_CATEGORIES = "CUSTOM_LIST_CATEGORIES"
        const val SHARED_PREFS_RECIPE = "SHARED_PREFS_RECIPE"
        const val IMAGE_FROM_ADD_FORM = "IMAGE_FROM_ADD_FORM"
    }
}