package com.example.smoothie.domain.models

interface IRecipeModel {
    var idRecipe: Int
    val name: String
    val ingredients: String
    val recipe: String
    val description: String
    val imageUrl: String
    var isFavorite: Boolean

    var inProgress: Boolean
    fun map(): HashMap<String,Any>
}