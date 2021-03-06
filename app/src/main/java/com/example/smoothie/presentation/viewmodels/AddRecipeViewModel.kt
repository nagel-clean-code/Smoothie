package com.example.smoothie.presentation.viewmodels

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smoothie.Constants.Companion.FORM_ADD_RECIPE
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.domain.usecase.database.SaveImageInDBUseCase
import com.example.smoothie.domain.usecase.database.SaveRecipeToDbUseCase
import com.example.smoothie.domain.usecase.sharedpref.recipe.GetImageFromAddFormSharPrefUseCase
import com.example.smoothie.domain.usecase.sharedpref.recipe.GetRecipeFromSharPrefUseCase
import com.example.smoothie.domain.usecase.sharedpref.recipe.SaveImageFromAddFormSharPrefUseCase
import com.example.smoothie.domain.usecase.sharedpref.recipe.SaveRecipeSharPrefUseCase
import com.example.smoothie.domain.usecase.sharedpref.sesion.GetUserNameFromSharPrefUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.reflect.KFunction1


@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val saveRecipeSharPrefUseCase: SaveRecipeSharPrefUseCase,
    private val getRecipeSharPrefUseCase: GetRecipeFromSharPrefUseCase,
    private val getUserNameFromSharPrefUseCase: GetUserNameFromSharPrefUseCase,
    private val saveRecipeToDbUseCase: SaveRecipeToDbUseCase,
    private val saveImageFromAddFormSharPrefUseCase: SaveImageFromAddFormSharPrefUseCase,
    private val getImageFromAddFormSharPrefUseCase: GetImageFromAddFormSharPrefUseCase,
    private val saveImageInDBUseCase: SaveImageInDBUseCase
) : BaseViewModel() {

    private var _recipeDisplayLiveDataMutable = MutableLiveData<IRecipeModel>()
    val recipeDisplayLiveDataMutable: LiveData<IRecipeModel> = _recipeDisplayLiveDataMutable

    private var _imageLiveDataMutable = MutableLiveData<Drawable?>()
    val resultImageLiveDataMutable: MutableLiveData<Drawable?> = _imageLiveDataMutable

    fun setRecipeToDataBase(recipe: IRecipeModel, image: ByteArray) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (!image.contentEquals("".toByteArray())) {
                    recipe.imageUrl = saveImageToDataBase(image)
                }
                saveRecipeToDbUseCase.execute(recipe)
            }
            _imageLiveDataMutable.value = null
        }
    }

    fun getUserName() = getUserNameFromSharPrefUseCase.execute()

    fun saveRecipeInSharedPrefs(recipe: IRecipeModel) = saveRecipeSharPrefUseCase.execute(recipe, FORM_ADD_RECIPE)

    fun saveImageInSharPref(image: Drawable?, convertImageToString: (Drawable) -> String) {
        _imageLiveDataMutable.value = image
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                if (image == null) {
                    saveImageFromAddFormSharPrefUseCase.execute("")
                } else {
                    saveImageFromAddFormSharPrefUseCase.execute(convertImageToString(image))
                }
            }
        }
    }

    private suspend fun saveImageToDataBase(image: ByteArray): String {
        return saveImageInDBUseCase.execute(image)
    }

    fun load() {
        _recipeDisplayLiveDataMutable.value = getRecipeSharPrefUseCase.execute(FORM_ADD_RECIPE)
    }


    fun loadImage(convertStringToImage: KFunction1<String, Drawable>) = viewModelScope.launch {
        var drawableImage: Drawable? = null
        withContext(Dispatchers.Default) {
            val stringImage = getImageFromAddFormSharPrefUseCase.execute()
            if (stringImage.isNotBlank())
                drawableImage = convertStringToImage(stringImage)
        }
        _imageLiveDataMutable.value = drawableImage
    }
}