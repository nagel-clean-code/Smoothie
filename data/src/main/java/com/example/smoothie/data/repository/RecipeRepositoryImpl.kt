package com.example.smoothie.data.repository

import com.example.smoothie.data.storage.RecipeStorage
import com.example.smoothie.domain.models.Ingredients
import com.example.smoothie.domain.repository.RecipeRepository


class RecipeRepositoryImpl(private val recipe: RecipeStorage) : RecipeRepository {

    override fun saveNameRecipe(name: String){
        recipe.saveNameRecipe(name)
    }

    override fun getNameRecipe(): String {
        return recipe.getNameRecipe()
    }

    override fun saveIngredients(ingredients: Ingredients) {
        recipe.saveIngredients(ingredients)
    }

    override fun getIngredients(): Ingredients {
        return recipe.getIngredients()
    }

    override fun saveRecipe(recipe: String) {
        TODO("Not yet implemented")
    }

    override fun saveDescription(description: String) {
        TODO("Not yet implemented")
    }

}