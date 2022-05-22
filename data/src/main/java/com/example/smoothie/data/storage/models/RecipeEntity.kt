package com.example.smoothie.data.storage.models

import com.example.smoothie.domain.models.IRecipeModel
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class RecipeEntity(
    override var idRecipe: String = "",
    override val name: String = "",
    override val ingredients: String ="",
    override val recipe: String = "",
    override val description: String = "",
    override var imageUrl: String = ""

): IRecipeModel{
    override fun map(): HashMap<String,String>{
        return hashMapOf(
            "idRecipe" to idRecipe,
            "name" to name,
            "ingredients" to ingredients,
            "recipe" to recipe,
            "description" to description,
            "imageUrl" to imageUrl
        )
    }
}
