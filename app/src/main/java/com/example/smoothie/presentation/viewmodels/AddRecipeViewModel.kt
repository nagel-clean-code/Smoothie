package com.example.smoothie.presentation.viewmodels

import android.content.ContentValues
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smoothie.Constants.Companion.FORM_ADD_RECIPE
import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.domain.usecase.database.SaveImageInDBUseCase
import com.example.smoothie.domain.usecase.database.SaveRecipeToDbUseCase
import com.example.smoothie.domain.usecase.sharedpref.recipe.*
import com.example.smoothie.domain.usecase.sharedpref.sesion.GetUserNameFromSharPrefUseCase
import com.example.smoothie.presentation.views.ApiWorkWithDataForBordCategories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.reflect.KFunction1


@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val saveCostumeCategoriesListSharPrefsUseCase: SaveCostumeCategoriesListSharPrefsUseCase,
    private val getCustomCategoriesListFromSharPrefsUseCase: GetCustomCategoriesListFromSharPrefsUseCase,
    private val saveRecipeSharPrefUseCase: SaveRecipeSharPrefUseCase,
    private val getRecipeSharPrefUseCase: GetRecipeFromSharPrefUseCase,
    private val getUserNameFromSharPrefUseCase: GetUserNameFromSharPrefUseCase,
    private val saveRecipeToDbUseCase: SaveRecipeToDbUseCase,
    private val saveImageFromAddFormSharPrefUseCase: SaveImageFromAddFormSharPrefUseCase,
    private val getImageFromAddFormSharPrefUseCase: GetImageFromAddFormSharPrefUseCase,
    private val saveImageInDBUseCase: SaveImageInDBUseCase
) : BaseViewModel(), ApiWorkWithDataForBordCategories {

    private var _recipeDisplayLiveDataMutable = MutableLiveData<IRecipeModel>()
    val recipeDisplayLiveDataMutable: LiveData<IRecipeModel> = _recipeDisplayLiveDataMutable

    private var _imageLiveDataMutable = MutableLiveData<Drawable?>()
    val resultImageLiveDataMutable: MutableLiveData<Drawable?> = _imageLiveDataMutable

    fun setRecipeToDataBase(recipe: IRecipeModel, image: ByteArray) {
        val handler = CoroutineExceptionHandler { _, throwable ->
            Log.w(ContentValues.TAG, throwable)
        }
        viewModelScope.launch(handler) {
            withContext(Dispatchers.IO) {
                if (!image.contentEquals("".toByteArray())) {
                    recipe.imageUrl = saveImageToDataBase(image)
                }
                saveRecipeToDbUseCase.execute(recipe)
            }
            _imageLiveDataMutable.value = null
        }
    }

    override fun getListCustomCategoriesFromSharPrefs(): MutableList<String> {
        return getCustomCategoriesListFromSharPrefsUseCase.execute() ?: mutableListOf()
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

    override fun saveCategoryInSharPrefs(list: List<String>) {
        saveCostumeCategoriesListSharPrefsUseCase.execute(list)
    }

    override fun saveSelectedCategoriesInSharPrefs(list: List<String>) =
        saveCostumeCategoriesListSharPrefsUseCase.execute(list, SAVE_LAST_CATEGORY_SHAR_PREFS)

    override fun getSelectedCategoriesFromSharPrefs(): MutableList<String> {
        return getCustomCategoriesListFromSharPrefsUseCase.execute(SAVE_LAST_CATEGORY_SHAR_PREFS) ?: mutableListOf()
    }

    companion object {
        const val SAVE_LAST_CATEGORY_SHAR_PREFS = "SAVE_LAST_CATEGORY_SHAR_PREFS_FRAGMENT_ADD"
    }
}