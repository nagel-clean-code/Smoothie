package com.example.smoothie.domain.models

interface IRecipeModel {
    val idRecipe: String
    val name: String
    val ingredients: Ingredients
    val recipe: String
    val description: String
}