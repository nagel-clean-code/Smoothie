package com.example.smoothie.domain.models

interface IRecipeModel {
    var idRecipe: Int
    val name: String
    val ingredients: String
    val recipe: String
    val description: String
    val imageUrl: String
    fun map(): HashMap<String,Any>
}