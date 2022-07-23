package com.example.smoothie.domain.models

interface IRecipeModel {
    var idRecipe: Int
    val uniqueId: String?
    val name: String?
    val recipe: String?
    val listCategory: List<Int>?
    var imageUrl: String?
    var isFavorite: Boolean
    var inProgress: Boolean
    fun map(): HashMap<String,Any>
}