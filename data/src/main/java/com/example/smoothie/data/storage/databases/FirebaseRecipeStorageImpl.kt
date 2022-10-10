package com.example.smoothie.data.storage.databases

import android.content.ContentValues.TAG
import android.util.Log
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.domain.models.IRecipeModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*

class FirebaseRecipeStorageImpl(private val userName: () -> String) : RecipeStorageDB {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val storageRef = FirebaseStorage.getInstance("gs://smoothie-40dd3.appspot.com")
    private var firstInitCount = false
    private lateinit var linkedList1: MutableList<Int>
    private lateinit var linkedList2: MutableList<Int>
    private var errorResult: String = ""

    private suspend fun incrementCounter(): Int {
        firestore.collection("counters_recipes").document("${userName()}_current")
            .update("${userName()}_current", FieldValue.increment(1))
        if (counterBuf != -1)
            counterBuf += 1
        checkErrors()
        return getCurrentValueCounter().also { algorithmCounter(it) }
    }

    private suspend fun decrementCounter(): Int {
        firestore.collection("counters_recipes").document("${userName()}_current")
            .update("count_recipe", FieldValue.increment(-1))
        if (counterBuf != -1)
            counterBuf -= 1
        checkErrors()
        return getCurrentValueCounter().also { algorithmCounter(it) }
    }

    private var counterBuf: Int = -1    //Для экономии трафика уменьшаем количество запросов через локальное сохранение
    private suspend fun getCurrentValueCounter(): Int {
        if (counterBuf != -1)
            return counterBuf
        var count = 0
        val result = firestore.collection("counters_recipes").document("${userName()}_current")
            .get().addOnSuccessListener {
                count = it.data!!["count_recipe"] as Int
            }.addOnFailureListener {
                errorResult = it.message!!
            }
        result.await()
        counterBuf = count
        checkErrors()
        return count
    }

    private fun algorithmCounter(countRecipes: Int) {
        if (!firstInitCount) {
            firstInitCount = true
            linkedList1 = (1 until countRecipes).toMutableList()
            linkedList2 = LinkedList()
        } else {
            if (countRecipes < linkedList1.size + linkedList2.size) {
                linkedList1.remove(countRecipes + 1)
                linkedList2.remove(countRecipes + 1)
            } else {
                linkedList1.add(countRecipes)
            }
        }
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

    override suspend fun saveRecipe(recipe: IRecipeModel) {
        val countRecipes = incrementCounter()
        recipe.idRecipe = countRecipes + 1
        firestore.collection(userName()).add(recipe.map())
    }

    override suspend fun nextRecipe(): IRecipeModel {
        var recipe = RecipeEntity(name = "Рецептов нет")
        if (getCurrentValueCounter() == 0)
            return recipe
        val rand = nextRandom()
        Log.d(TAG, "Random:$rand")
        val result =
            firestore.collection(userName()).whereEqualTo("idRecipe", rand)
                .get().addOnSuccessListener { documentSnapshot ->
                    Log.d(TAG, "Количество найденых рецептов: ${documentSnapshot.size()}")
                    try {
                        recipe = documentSnapshot.documents.first().toObject<RecipeEntity>()!!
                    } catch (e: Exception) {
                        errorResult = e.message!!
                    }
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        result.await()
        checkErrors()
        return recipe
    }

    private fun checkErrors() {
        if (errorResult.isNotBlank()) {
            Log.d(TAG, errorResult)
            val buf = errorResult
            errorResult = ""
            throw Exception(buf)
        }
    }

    override suspend fun saveImage(imageByteArray: ByteArray): String {
        val urlImage = "hats_recipes/${userName()}/image_${UUID.randomUUID()}.jpg"
        val riversRef = storageRef.reference.child(urlImage)
        riversRef.putBytes(imageByteArray).addOnFailureListener {
            Log.w(TAG, "Не удалось загрузить изображение в БД: ", it)
        }.addOnSuccessListener {
            Log.d(TAG, "Изображение успешно загружено")
        }
        return urlImage
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
            errorResult = "Не удалось удалить изображение, url:$url"
        }
    }

    override suspend fun getRecipes(searchBy: String, first: Int, last: Int): List<IRecipeModel> {
        val recipes = mutableListOf<RecipeEntity>()
        if (getCurrentValueCounter() == 0)
            return recipes

        val result =
            firestore.collection(userName())
                .whereGreaterThanOrEqualTo("name", searchBy)
                .get().addOnSuccessListener { documentSnapshot ->
                    Log.d(
                        TAG,
                        "Количество найденых рецептов для RecycleView: ${documentSnapshot.size()}"
                    )
                    for (i in first - 1 until last) {
                        if (i >= documentSnapshot.documents.size) break
                        val currentDocument = documentSnapshot.documents[i]
                        val recipe: RecipeEntity? =
                            currentDocument.toObject()  //Почему то isFavorite не преобразовывалась, пришлось вручную
                        recipe?.isFavorite = currentDocument.data?.get("isFavorite") as Boolean
                        if (recipe != null) {
                            recipes.add(recipe)
                        } else {
                            errorResult = "Не удалось распарсить рецепт!"
                        }
                    }
                    Log.d(TAG, "Возвращаю ${recipes.size} рцептов")
                }.addOnFailureListener { exception ->
                    errorResult = "Error getting documents: $exception"
                }
        result.await()
        checkErrors()
        return recipes
    }

    override suspend fun saveFavoriteFlag(idRecipe: Int, flag: Boolean) {
        val result =
            firestore.collection(userName())
                .whereEqualTo("idRecipe", idRecipe)
                .get().addOnSuccessListener {
                    if (it.documents.size == 0) {
                        errorResult = "Документ с таким id:($idRecipe) не найден!"
                    } else {
                        it.documents.first().reference.update("isFavorite", flag)
                            .addOnSuccessListener { Log.d(TAG, "Документ id($idRecipe) обновлён") }
                            .addOnFailureListener {
                                errorResult = "Не удалось обновить документ id($idRecipe)"
                            }
                    }
                }.addOnFailureListener {
                    errorResult = "Не удалось сделать поиск по id: $idRecipe"
                }
        result.await()
        checkErrors()
    }

    private fun updateIndexes(deleteIx: Int, countRecipes: Int) {
        if (deleteIx == countRecipes) return

        firestore.collection(userName())
            .whereEqualTo("idRecipe", countRecipes)
            .get().addOnSuccessListener {
                if (it.documents.size > 0) {
                    Log.d(TAG, "Документ для индексирования найден")
                    it.documents.first().reference.update("idRecipe", deleteIx)
                        .addOnSuccessListener { Log.d(TAG, "Успешная переиндексация") }
                        .addOnFailureListener {
                            errorResult = "Ошибка при обновлении индекса в процессе индексации"
                        }
                } else {
                    errorResult = "Документ для индексирования не найден"
                }
            }
            .addOnFailureListener {
                errorResult = "Ошибка при запросе поиска элемента в базе"
            }
    }

    override suspend fun deleteRecipe(idRecipe: Int) {
        val countRecipes = getCurrentValueCounter()
        val result =
            firestore.collection(userName())
                .whereEqualTo("idRecipe", idRecipe)
                .get().addOnSuccessListener {
                    if (it.documents.size == 0) {
                        errorResult = "Документ с таким id:($idRecipe) не найден!"
                    } else {
                        val doc = it.documents.first()
                        updateIndexes(idRecipe, countRecipes)
                        doc.getString("imageUrl")?.let { it1 -> deleteImage(it1) }
                        doc.reference.delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "Документ удалён!")
                            }
                            .addOnFailureListener {
                                errorResult = "Не удалось удалить документ id($idRecipe)"
                            }
                    }
                }.addOnFailureListener {
                    errorResult = "Не удалось сделать поиск по id: $idRecipe, для удаления"
                }
        result.await()
        if (errorResult.isBlank()) {
            decrementCounter()
        }
        checkErrors()
    }

    private companion object {
        const val MAX_SIZE_PICTURE: Long = 1024 * 1024 * 2
    }
}
