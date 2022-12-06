package com.example.smoothie.data.storage.databases

import android.content.ContentValues.TAG
import android.util.Log
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.domain.models.IRecipeModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*

class FirebaseRecipeStorageImpl(private val getUserName: () -> String) : RecipeStorageDB {
    private val myNickname = getUserName.invoke()

    private val firestore: FirebaseFirestore = Firebase.firestore
    private val storageRef = FirebaseStorage.getInstance("gs://smoothie-40dd3.appspot.com")
    private val realtimeDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val firebaseCounterRecipes = FirebaseCounterRecipes(
        myNickname,
        realtimeDatabase,
        firestore
    )
    private var errorResult: String = ""

    override suspend fun saveRecipe(recipe: IRecipeModel) {
        firebaseCounterRecipes.incrementCounter() {
            recipe.idRecipe = (it + 1).toInt()  //+1 потому что нумерация не с 0
            firestore.collection(getUserName()).add(recipe.map())
        }
    }

    override suspend fun nextRecipe(): IRecipeModel {
        var recipe = RecipeEntity(name = "Рецептов нет")
        if (firebaseCounterRecipes.getCurrentCountRecipes() == 0L)
            return recipe
        val rand = firebaseCounterRecipes.nextRandom()
        Log.d(TAG, "Random:$rand")
        val name = getUserName()
        val result =
            firestore.collection(name).whereEqualTo("idRecipe", rand)
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
        } else {
            errorResult = firebaseCounterRecipes.errorResult
            if (errorResult.isNotBlank()) {
                checkErrors()
            }
        }
    }

    override suspend fun saveImage(imageByteArray: ByteArray): String {
        val urlImage = "hats_recipes/${getUserName()}/image_${UUID.randomUUID()}.jpg"
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
        if (firebaseCounterRecipes.getCurrentCountRecipes() == 0L)
            return recipes

        val result =
            firestore.collection(getUserName())
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
            firestore.collection(getUserName())
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

    private fun updateIndexes(deleteIx: Long, countRecipes: Long) {
        if (deleteIx == countRecipes) return

        firestore.collection(getUserName())
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
        val countRecipes = firebaseCounterRecipes.getCurrentCountRecipes()
        val result =
            firestore.collection(getUserName())
                .whereEqualTo("idRecipe", idRecipe)
                .get().addOnSuccessListener {
                    if (it.documents.size == 0) {
                        errorResult = "Документ с таким id:($idRecipe) не найден!"
                    } else {
                        val doc = it.documents.first()
                        updateIndexes(idRecipe.toLong(), countRecipes)
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
            firebaseCounterRecipes.decrementCounter()
        }
        checkErrors()
    }

    private companion object {
        const val MAX_SIZE_PICTURE: Long = 1024 * 1024 * 2
    }
}
