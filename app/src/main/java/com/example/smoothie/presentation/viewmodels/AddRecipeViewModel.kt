package com.example.smoothie.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smoothie.domain.usecase.GetIngredientsUseCase
import com.example.smoothie.domain.usecase.GetNameRecipeUseCase
import com.example.smoothie.domain.usecase.SaveIngredientsUseCase
import com.example.smoothie.domain.usecase.SaveNameRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val saveIngredientsUseCase: SaveIngredientsUseCase,
    private val getIngredientsUseCase: GetIngredientsUseCase,
    private val saveNameRecipeUseCase: SaveNameRecipeUseCase,
    private val getNameRecipeUseCase: GetNameRecipeUseCase
) : ViewModel() {

    private var ingredientsLiveDataMutable = MutableLiveData<String>()
    val resultIngredientsLiveDataMutable: LiveData<String> = ingredientsLiveDataMutable  //Ограничение доступа из внешнего класса

    private var nameLiveDataMutable = MutableLiveData<String>()
    val resultNameLiveDataMutable: LiveData<String> = nameLiveDataMutable  //Ограничение доступа из внешнего класса

    override fun onCleared() {
        super.onCleared()

    }

    fun saveIngredients(text: String) {
        saveIngredientsUseCase.execute(text)
        ingredientsLiveDataMutable.value = text
    }

    fun saveName(text: String) {
        Log.e("saveName","Сохраняем в кэш и лайфдату")
        saveNameRecipeUseCase.execute(text)
        nameLiveDataMutable.value = text
    }


    fun loadName() {
        Log.e("loadName","Загружаем в лайфдату из кэша")
        nameLiveDataMutable.value = getNameRecipeUseCase.execute()
    }

}