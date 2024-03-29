package com.example.smoothie.data.storage.databases.external

import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.math.floor

class SessionStorageFbImpl : SessionStorageFB {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val realtimeDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

    override suspend fun createNewUserAccount(): String {
        val ref = firestore.collection("USERS").document(PATH_USER_COUNT)
        incrementCounter(ref, WIDTH_COUNTER)
        val username = "user_${getCount(ref)}"
        createCountRecipe(username)
        return username
    }

    /**
     *  создаю два счётчика, один для получения значения в реальном времени,
     *  второй для сверки данных, если вдруг где то произошёл сбой
     *  */
    private fun createCountRecipe(name: String) {
        realtimeDatabase.child("counters").child("${name}_current").child("count_recipe").setValue(0)
            .addOnSuccessListener { Log.d(TAG, "Realtime Счётчик успешно добавлен") }
            .addOnFailureListener { e -> Log.w(TAG, "Не удалось создать realtime счётчик", e) }

        firestore.collection("counters").document("${name}_current")
            .set(mapOf("count_recipe" to 0))
            .addOnSuccessListener { Log.d(TAG, "Счётчик успешно добавлен") }
            .addOnFailureListener { e -> Log.w(TAG, "Не удалось создать счётчик, крах", e) }
    }

    private suspend fun getCount(ref: DocumentReference): Int { //FIXME - выяснить для чего это
        var count = 0
        ref.collection("shards").get()
            .continueWith { task ->
                for (snap in task.result!!) {
                    val shard = snap.toObject<Shard>()
                    count += shard.count
                }
                count
            }.await()
        return count
    }

    private suspend fun incrementCounter(ref: DocumentReference, numShards: Int) {
        val shardId = floor(Math.random() * numShards).toInt()
        val shardRef = ref.collection("shards").document(shardId.toString())
        shardRef.update("count", FieldValue.increment(1))
            .addOnSuccessListener { Log.d(TAG, "Счётчик успешно обновлён") }
            .addOnFailureListener { e -> Log.w(TAG, "Не удалось обновить счётчик", e) }.await()
    }

    // использовался только 1 раз для создания счётчика
    private fun createCounter(ref: DocumentReference, numShards: Int): Task<Void> {
        // Initialize the counter document, then initialize each shard.
        return ref.set(Counter(numShards))
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }

                val tasks = arrayListOf<Task<Void>>()

                // Initialize each shard with count=0
                for (i in 0 until numShards) {
                    val makeShard = ref.collection("shards")
                        .document(i.toString())
                        .set(Shard(0))

                    tasks.add(makeShard)
                }

                Tasks.whenAll(tasks)
            }
    }

    companion object {
        const val WIDTH_COUNTER = 10
        const val PATH_USER_COUNT = "count_user"
    }
}

// counters/${ID}
data class Counter(var numShards: Int = -1)

// counters/${ID}/shards/${NUM}
data class Shard(var count: Int = -1)