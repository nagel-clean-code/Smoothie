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
    private val firestore: FirebaseFirestore = Firebase.firestore
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
                    if(countRecipes < linkedList1.size + linkedList2.size){
                        linkedList1.remove(countRecipes+1)
                        linkedList2.remove(countRecipes+1)
                    }else{
                        linkedList1.add(countRecipes)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        }
        dataBaseCounter.addValueEventListener(obj)
        dataBaseCounter.addListenerForSingleValueEvent(obj)
    }

    private var prevNumber = -1
    private fun nextRandom(): Int {
        var number: Int
        do {
            number = linkedList1.random()
        } while (linkedList1.size != 1 && number == prevNumber)
        prevNumber = number
        linkedList2.add(number)
        linkedList1.remove(number)

        if (linkedList1.isEmpty()) {
            linkedList1.addAll(linkedList2)
            linkedList2.clear()
        }
        return number
    }

    override fun saveRecipe(recipe: IRecipeModel) {
        recipe.idRecipe = countRecipes + 1
        firestore.collection(userName).add(recipe.map())
        dataBaseCounter.child(userName).setValue(countRecipes + 1)
    }

    override suspend fun nextRecipe(): IRecipeModel {
        var recipe = RecipeEntity(-1, "Рецептов нет", "", "", "")
        if (countRecipes == 0)
            return recipe
        val rand = nextRandom()
        Log.d(TAG, "Random:$rand")
        val result =
            firestore.collection(userName).whereEqualTo("idRecipe", rand)
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
        val imageRef = storageRef.reference.child(url)

        val up = imageRef.getBytes(MAX_SIZE_PICTURE).addOnSuccessListener {
            Log.d(TAG, "Изображение успешно загружено")
        }.addOnFailureListener {
            Log.w(TAG, "Не удалось загрузить изображение из БД")
        }
        return up.await()
    }

    private fun deleteImage(url: String) {
        if (url.isBlank()) return

        val imageRef = storageRef.reference.child(url)
        imageRef.delete().addOnSuccessListener {
            Log.d(TAG, "Изображение успешно удалено, url:$url")
        }.addOnFailureListener {
            Log.w(TAG, "Не удалось удалить изображение, url:$url", it)
            throw Exception("Не удалось удалить изображение, url:$url")
        }
    }

    override suspend fun getRecipes(searchBy: String, first: Int, last: Int): List<IRecipeModel> {
        val recipes = mutableListOf<RecipeEntity>()
        if (countRecipes == 0)
            return recipes

        val result =
            firestore.collection(userName)
                .whereGreaterThanOrEqualTo("name", searchBy)
                .get().addOnSuccessListener { documentSnapshot ->
                    Log.d(TAG, "Количество найденых рецептов для RecycleView: ${documentSnapshot.size()}")
                    for (i in first-1 until last) {
                        if(i >= documentSnapshot.documents.size) break
                        val currentDocument = documentSnapshot.documents[i]
                        val recipe: RecipeEntity? = currentDocument.toObject()  //Почему то isFavorite не преобразовывалась, пришлось вручную
                        recipe?.isFavorite = currentDocument.data?.get("isFavorite") as Boolean
                        if (recipe != null) {
                            recipes.add(recipe)
                        }else{
                            Log.w(TAG,"Не удалось распарсить рецепт!")
                        }
                    }
                    Log.d(TAG, "Возвращаю ${recipes.size} рцептов")
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        result.await()
        return recipes
    }

    override suspend fun saveFavoriteFlag(idRecipe: Int, flag: Boolean) {
        val result =
            firestore.collection(userName)
                .whereEqualTo("idRecipe", idRecipe)
                .get().addOnSuccessListener {
                    if (it.documents.size == 0) {
                        Log.d(TAG, "Документ с таким id:($idRecipe) не найден!")
                        throw Exception("Документ с таким id:($idRecipe) не найден!")

                    } else {
                        it.documents.first().reference.update("isFavorite", flag)
                            .addOnSuccessListener { Log.d(TAG, "Документ id($idRecipe) обновлён") }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Не удалось обновить документ id($idRecipe)", e)
                                throw Exception("Не удалось обновить документ id($idRecipe)", e)
                            }
                    }
                }.addOnFailureListener { e ->
                    Log.w(TAG, "Не удалось сделать поиск по id: $idRecipe", e)
                    throw Exception("Не удалось сделать поиск по id: $idRecipe", e)
                }
        result.await()
    }

    private fun updateIndexes(deleteIx: Int) {
        if (deleteIx == countRecipes) return

        firestore.collection(userName)
            .whereEqualTo("idRecipe", countRecipes)
            .get().addOnSuccessListener {
                if (it.documents.size > 0) {
                    Log.d(TAG, "Документ для индексирования найден")
                    it.documents.first().reference.update("idRecipe", deleteIx)
                        .addOnSuccessListener { Log.d(TAG, "Успешная переиндексация") }
                        .addOnFailureListener {
                            Log.w(TAG, "Ошибка при обновлении индекса в процессе индексации")
                            throw Exception("Не удалось выполнить переиндексацию")
                        }
                } else {
                    Log.w(TAG, "Документ для индексирования не найден")
                    throw Exception("Не удалось выполнить переиндексацию")
                }
            }
            .addOnFailureListener {
                throw Exception("Ошибка при запросе поиска элемента в базе")
            }
    }

    override suspend fun deleteRecipe(idRecipe: Int) {
        val result =
            firestore.collection(userName)
                .whereEqualTo("idRecipe", idRecipe)
                .get().addOnSuccessListener {
                    if (it.documents.size == 0) {
                        Log.d(TAG, "Документ с таким id:($idRecipe) не найден!")
                        throw Exception("Документ с таким id:($idRecipe) не найден!")
                    } else {
                        val doc = it.documents.first()
                        updateIndexes(idRecipe)
                        doc.getString("imageUrl")?.let { it1 -> deleteImage(it1) }
                        doc.reference.delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "Документ удалён!")
                                dataBaseCounter.child(userName).setValue(countRecipes - 1)
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Не удалось удалить документ id($idRecipe)", e)
                                throw Exception("Не удалось удалить документ id($idRecipe)", e)
                            }
                    }
                }.addOnFailureListener { e ->
                    Log.w(TAG, "Не удалось сделать поиск по id: $idRecipe, для удаления", e)
                    throw Exception("Не удалось сделать поиск по id: $idRecipe, для удаления", e)
                }
        result.await()
    }

    private companion object {
        const val MAX_SIZE_PICTURE: Long = 1024 * 1024 * 2
    }
}
