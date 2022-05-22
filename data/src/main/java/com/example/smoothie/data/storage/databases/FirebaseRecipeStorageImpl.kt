package com.example.smoothie.data.storage.databases

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.domain.models.IRecipeModel
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

class FirebaseRecipeStorageImpl(private val userName: String) : RecipeStorageDB {
    private var countRecipes: Int = 1
    private val dataBaseCounter: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val dataBase: FirebaseFirestore = Firebase.firestore
    private val storageRef = FirebaseStorage.getInstance("gs://smoothie-40dd3.appspot.com")

    init {
        //FIXME переделать на firestore, .update("population", FieldValue.increment(50))
        val obj = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                countRecipes = dataSnapshot.child(userName).getValue(Int::class.java)!!
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        }
        dataBaseCounter.addValueEventListener(obj)
        dataBaseCounter.addListenerForSingleValueEvent(obj)
    }

    override fun saveRecipe(recipe: IRecipeModel) {
        recipe.idRecipe = (countRecipes + 1).toString()
        dataBase.collection(userName).add(recipe.map())
        dataBaseCounter.child(userName).setValue(countRecipes + 1)
    }

    override suspend fun nextRecipe(): IRecipeModel {
        var recipe: RecipeEntity = RecipeEntity("Рецепт не найден", "Ошибка", "", "", "")
        val result =
            dataBase.collection(userName).whereEqualTo("idRecipe", "${(0..countRecipes).random()}")
                .get().addOnSuccessListener { documentSnapshot ->
                    Log.d(TAG, "Количество найденых рецептов: ${documentSnapshot.size()}")
                    recipe = documentSnapshot.documents.first().toObject<RecipeEntity>()!!
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        result.await()
        return recipe
    }

    override suspend fun saveImage(imagePatch: String): String {
        val riversRef = storageRef.reference.child("/hats_recipes/image_${countRecipes+1}")
        riversRef.putFile(Uri.parse(imagePatch))
            .addOnFailureListener {
                Log.w(TAG, "Не удалось загрузить изображение в БД: ", it)
            }.addOnSuccessListener { taskSnapshot ->
                Log.d(TAG, "Изображение успешно загружено")
            }
        return riversRef.downloadUrl.await().toString()
    }
}