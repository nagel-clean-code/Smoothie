package com.example.smoothie.data.storage.databases

import com.example.smoothie.domain.models.IRecipeModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

const val GROUP_KEY = "Recipe"

class FirebaseRecipeStorageImpl: RecipeStorageDB {

    private val dataBase: DatabaseReference = FirebaseDatabase.getInstance().getReference(GROUP_KEY)

    override fun saveRecipe(recipe: IRecipeModel){
        dataBase.push().setValue(recipe)
    }
}