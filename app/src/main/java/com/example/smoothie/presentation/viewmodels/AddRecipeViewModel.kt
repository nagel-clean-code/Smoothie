package com.example.smoothie.presentation.viewmodels

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.domain.usecase.database.SaveImageInDBUseCase
import com.example.smoothie.domain.usecase.sharedpref.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val saveIngredientsInSharPrefUseCase: SaveIngredientsInSharPrefUseCase,
    private val getIngredientsFromSharPrefUseCase: GetIngredientsFromSharPrefUseCase,
    private val saveNameRecipeInSharPrefUseCase: SaveNameRecipeInSharPrefUseCase,
    private val getNameRecipeFromSharPrefUseCase: GetNameRecipeFromSharPrefUseCase,
    private val saveRecipeToDbUseCase: SaveRecipeToDbUseCase,
    private val saveImageFromAddFormSharPrefUseCase: SaveImageFromAddFormSharPrefUseCase,
    private val getImageFromAddFormSharPrefUseCase: GetImageFromAddFormSharPrefUseCase,
    private val saveImageInDBUseCase: SaveImageInDBUseCase
) : BaseViewModel() {

    private var _ingredientsLiveDataMutable = MutableLiveData<String>()
    val resultIngredientsLiveDataMutable: LiveData<String> = _ingredientsLiveDataMutable

    private var _nameLiveDataMutable = MutableLiveData<String>()
    val resultNameLiveDataMutable: LiveData<String> = _nameLiveDataMutable

    private var _imageLiveDataMutable = MutableLiveData<Drawable>()
    val resultImageLiveDataMutable: LiveData<Drawable> = _imageLiveDataMutable

    var uriImage: String = ""

    fun setRecipeToDataBase(recipe: RecipeEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if(uriImage.isNotBlank()) {
                    recipe.imageUrl = saveImageToDataBase()
                }
                saveRecipeToDbUseCase.execute(recipe)
            }
        }
    }

    fun saveIngredients(text: String) {
        saveIngredientsInSharPrefUseCase.execute(text)
        _ingredientsLiveDataMutable.value = text
    }

    fun saveName(text: String) {
        saveNameRecipeInSharPrefUseCase.execute(text)
        _nameLiveDataMutable.value = text
    }

    fun saveImageInSharPref(image: Drawable, convertImageToString: (Drawable) -> String) {
        _imageLiveDataMutable.value = image
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                saveImageFromAddFormSharPrefUseCase.execute(convertImageToString(image))
            }
        }
    }

    private suspend fun saveImageToDataBase(): String = saveImageInDBUseCase.execute(uriImage)


    fun load() {
        loadName()
        loadIngredients()
    }

    private fun loadName() {
        _nameLiveDataMutable.value = getNameRecipeFromSharPrefUseCase.execute()
    }

    private fun loadIngredients() {
        _ingredientsLiveDataMutable.value = getIngredientsFromSharPrefUseCase.execute()
    }

    fun loadImage(convertStringToImage: (String) -> Drawable) {
        val stringImage = getImageFromAddFormSharPrefUseCase.execute()
        if (stringImage.isNotBlank())
            _imageLiveDataMutable.value = convertStringToImage(stringImage)
    }


}