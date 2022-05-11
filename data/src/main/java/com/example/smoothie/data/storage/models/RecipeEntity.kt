package com.example.smoothie.data.storage.models

import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.domain.models.Ingredients


data class RecipeEntity(
    override val idRecipe: String,
    override val name: String,
    override val ingredients: Ingredients,
    override val recipe: String,
    override val description: String
): IRecipeModel
