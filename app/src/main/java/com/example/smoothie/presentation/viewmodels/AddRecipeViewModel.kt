package com.example.smoothie.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smoothie.domain.usecase.GetIngredients
import com.example.smoothie.domain.usecase.GetNameRecipe
import com.example.smoothie.domain.usecase.SaveIngredients
import com.example.smoothie.domain.usecase.SaveNameRecipe

class AddRecipeViewModel(
    private val saveIngredients: SaveIngredients,
    private val getIngredients: GetIngredients,
    private val saveNameRecipe: SaveNameRecipe,
    private val getNameRecipe: GetNameRecipe
) : ViewModel() {

    private var ingredientsLiveDataMutable = MutableLiveData<String>()
    val resultIngredientsLiveDataMutable: LiveData<String> = ingredientsLiveDataMutable  //Ограничение доступа из внешнего класса

    private var nameLiveDataMutable = MutableLiveData<String>()
    val resultNameLiveDataMutable: LiveData<String> = nameLiveDataMutable  //Ограничение доступа из внешнего класса

    override fun onCleared() {
        super.onCleared()

    }

    fun saveIngredients(text: String) {
        saveIngredients.execute(text)
        ingredientsLiveDataMutable.value = text
    }

    fun saveName(text: String) {
        Log.e("saveName","Сохраняем в кэш и лайфдату")
        saveNameRecipe.execute(text)
        nameLiveDataMutable.value = text
    }


    fun loadName() {
        Log.e("loadName","Загружаем в лайфдату из кэша")
        nameLiveDataMutable.value = getNameRecipe.execute()
    }

}