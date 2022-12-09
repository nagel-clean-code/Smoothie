package com.example.smoothie.data.storage.databases

import android.content.ContentValues
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class FirebaseCounterRecipes(
    private val getUserName: () -> String,
    private val realtimeDatabase: DatabaseReference,
    private val firestore: FirebaseFirestore
) {

    private val patchFieldCounterRecipes by lazy {
        realtimeDatabase
            .child("counters")
            .child("${getUserName.invoke()}_current")
            .child("count_recipe")
    }
    private val patchCountRecipeFirestore: DocumentReference by lazy {
        firestore.collection("counters").document("${getUserName.invoke()}_current")
    }

    private val countRecipes = AtomicInteger(0)
    private var firstInitCount = false
    private lateinit var linkedList1: MutableList<Long>
    private lateinit var linkedList2: MutableList<Long>
    var errorResult: String = ""
        get() {
            val buf = field
            field = ""
            return buf
        }

    private val counterRealTimeRecipes = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dataSnapshot.getValue(Int::class.java)?.let {
                Log.d(ContentValues.TAG, "Текущее количество рецептов: $it")
                countRecipes.set(it)
                algorithmCounter(it.toLong())
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(ContentValues.TAG, "Отмена чтения текущего количест ва рецептов", databaseError.toException())
        }
    }

    init {
        patchFieldCounterRecipes.addValueEventListener(counterRealTimeRecipes)
    }

    private fun getCurrentValueCounterRecipes(updateRealtimeCounter: (Int) -> Unit) {
        val result = firestore.collection("counters")
            .document("${getUserName.invoke()}_current")
            .get()
            .addOnSuccessListener {
                val res = it.data?.get("count_recipe") as Long
                updateRealtimeCounter.invoke(res.toInt())
            }.addOnFailureListener {
                errorResult = it.message!!
            }
    }

    /**
     * Методы реализуют гарантированное увеличение счётчика на 1
     * И выполнение кода с реальным результатом увеличения
     *
     * FIXME нужно переделать на выполнение в виде единой транзакции
     */
    suspend fun incrementCounter(performCode: (Long) -> Unit = {}) {
        patchCountRecipeFirestore.update("count_recipe", FieldValue.increment(1))
            .addOnSuccessListener {
                getCurrentValueCounterRecipes() {
                    performCode.invoke(it.toLong())
                    patchFieldCounterRecipes.setValue(it)
                }
            }.addOnFailureListener {
                errorResult = it.message!!
            }
    }

    /**
     * Методы реализуют гарантированное уменьшение счётчика на 1
     * И выполнение кода с реальным результатом уменьшения
     *
     * FIXME нужно переделать на выполнение в виде единой транзакции
     */
    suspend fun decrementCounter(performCode: (Long) -> Unit = {}) {
        patchCountRecipeFirestore.update("count_recipe", FieldValue.increment(-1))
            .addOnSuccessListener {
                getCurrentValueCounterRecipes() {
                    performCode.invoke(it.toLong())
                    patchFieldCounterRecipes.setValue(it)
                }
            }.addOnFailureListener {
                errorResult = it.message!!
            }
    }

    private fun algorithmCounter(countRecipes: Long) {
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

    private var prevNumber = -1L
    fun nextRandom(): Long {
        var number: Long
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

    fun getCurrentCountRecipes(): Long = countRecipes.get().toLong()
}