package com.example.smoothie.data.storage.databases

import android.content.ContentValues.TAG
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
import java.util.*

class FirebaseRecipeStorageImpl(private val userName: String) : RecipeStorageDB {
    private var countRecipes: Int = 1
    private val dataBaseCounter: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val dataBase: FirebaseFirestore = Firebase.firestore
    private val storageRef = FirebaseStorage.getInstance("gs://smoothie-40dd3.appspot.com")
    private var firstInitCount = false
    private lateinit var linkedList1: MutableList<Int>
    private lateinit var linkedList2: MutableList<Int>

    init {
        //FIXME переделать на firestore, .update("population", FieldValue.increment(50))
        val obj = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                countRecipes = dataSnapshot.child(userName).getValue(Int::class.java)!!
                if (!firstInitCount) {
                    firstInitCount = true
                    linkedList1 = (1 until countRecipes).toMutableList()
                    linkedList2 = LinkedList()
                } else {
                    linkedList1.add(countRecipes)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        }
        dataBaseCounter.addValueEventListener(obj)
        dataBaseCounter.addListenerForSingleValueEvent(obj)
    }

    //FIXME Плохо работает на малом количестве данных
    private val linkedList3 = LinkedList<Int>()
    private fun nextRandom(): Int {
        val number = linkedList1.random()
        val ca = countRecipes * 0.20
        val siz = linkedList1.size
        if (linkedList1.size < countRecipes * 0.20) { //если в массиве осталось всего 20% от первоначального
            linkedList3.add(number) // последние 20% сохраняем в резервный список
        } else {
            linkedList2.add(number)
        }
        linkedList1.remove(number)
        if (linkedList3.isNotEmpty()) { //Если есть резервные числа с последнего набора (с конца списка - 20%)
            if (linkedList1.size > (countRecipes * 0.20) && (linkedList1.size < countRecipes * 0.60)) {  // Если уже первые 20% нового списка отработали (новый список без последних 20%)
                linkedList1.addAll(linkedList3) //Добавляем в конец резервные 20%
                linkedList3.clear()             //очищаем резерв
            }
        }
        if (linkedList1.isEmpty()) {
            linkedList1.addAll(linkedList2)
            linkedList2.clear()
        }
        return number
    }

    override fun saveRecipe(recipe: IRecipeModel) {
        recipe.idRecipe = countRecipes + 1
        dataBase.collection(userName).add(recipe.map())
        dataBaseCounter.child(userName).setValue(countRecipes + 1)
    }

    override suspend fun nextRecipe(): IRecipeModel {
        var recipe = RecipeEntity(-1, "Ошибка", "", "", "")
        if (countRecipes == 0)
            return recipe
        val rand = nextRandom()
        Log.d(TAG, "Random:$rand")
        val result =
            dataBase.collection(userName).whereEqualTo("idRecipe", rand)
                .get().addOnSuccessListener { documentSnapshot ->
                    Log.d(TAG, "Количество найденых рецептов: ${documentSnapshot.size()}")
                    recipe = documentSnapshot.documents.first().toObject<RecipeEntity>()!!
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        result.await()
        return recipe
    }

    override suspend fun saveImage(imageByteArray: ByteArray): String {
        val riversRef = storageRef.reference.child("hats_recipes/image_${countRecipes + 1}.jpg")
        val up = riversRef.putBytes(imageByteArray).addOnFailureListener {
            Log.w(TAG, "Не удалось загрузить изображение в БД: ", it)
        }.addOnSuccessListener {
            Log.d(TAG, "Изображение успешно загружено")
        }
        return "hats_recipes/image_${countRecipes + 1}.jpg"
    }

    override suspend fun getImageByUrl(url: String): ByteArray {
        val islandRef = storageRef.reference.child(url)

        val up = islandRef.getBytes(MAX_SIZE_PICTURE).addOnSuccessListener {
            Log.d(TAG, "Изображение успешно загружено")
        }.addOnFailureListener {
            Log.w(TAG, "Не удалось загрузить изображение из БД")
        }
        return up.await()
    }

    override suspend fun getRecipes(first: Int, last: Int): List<IRecipeModel> {
        val recipe = mutableListOf<RecipeEntity>()
        if (countRecipes == 0)
            return recipe

        val result =
            dataBase.collection(userName)
                .whereGreaterThanOrEqualTo("idRecipe", first)
                .whereLessThanOrEqualTo("idRecipe", last)
                .get().addOnSuccessListener { documentSnapshot ->
                    Log.d(TAG, "Количество найденых рецептов для RecycleView: ${documentSnapshot.size()}")
                    for (document in documentSnapshot) {
                        recipe.add(document.toObject())
                    }
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        result.await()
        return recipe
    }

    private companion object{
        const val MAX_SIZE_PICTURE: Long = 1024 * 1024 * 2
    }
}
