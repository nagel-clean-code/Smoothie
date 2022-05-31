package com.example.smoothie.data.storage.models

import com.example.smoothie.domain.models.IRecipeModel
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class RecipeEntity(
    override var idRecipe: Int = -1,
    override val name: String = "",
    override val ingredients: String = "",
    override val recipe: String = "",
    override val description: String = "",
    override var imageUrl: String = "",
    override var isFavorite: Boolean = false,

    override var inProgress: Boolean = false
): IRecipeModel{
    override fun map(): HashMap<String,Any>{
        return hashMapOf(
            "idRecipe" to idRecipe,
            "name" to name,
            "ingredients" to ingredients,
            "recipe" to recipe,
            "description" to description,
            "imageUrl" to imageUrl,
            "isFavorite" to isFavorite
        )
    }
}
