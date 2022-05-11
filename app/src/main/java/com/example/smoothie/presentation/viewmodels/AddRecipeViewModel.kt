package com.example.smoothie.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val saveIngredientsUseCase: SaveIngredientsUseCase,
    private val getIngredientsUseCase: GetIngredientsUseCase,
    private val saveNameRecipeUseCase: SaveNameRecipeUseCase,
    private val getNameRecipeUseCase: GetNameRecipeUseCase,
    private val saveRecipeToDbUseCase: SaveRecipeToDbUseCase
) : ViewModel() {

    private var ingredientsLiveDataMutable = MutableLiveData<String>()
    val resultIngredientsLiveDataMutable: LiveData<String> = ingredientsLiveDataMutable  //Ограничение доступа из внешнего класса

    private var nameLiveDataMutable = MutableLiveData<String>()
    val resultNameLiveDataMutable: LiveData<String> = nameLiveDataMutable  //Ограничение доступа из внешнего класса

    fun addRecipeToDataBase(recipe: RecipeEntity){
        saveRecipeToDbUseCase.execute(recipe)
    }

    fun saveIngredients(text: String) {
        saveIngredientsUseCase.execute(text)
        ingredientsLiveDataMutable.value = text
    }

    fun saveName(text: String) {
        saveNameRecipeUseCase.execute(text)
        nameLiveDataMutable.value = text
    }


    fun loadName() {
        nameLiveDataMutable.value = getNameRecipeUseCase.execute()
    }

}