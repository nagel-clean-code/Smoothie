package com.example.smoothie.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.smoothie.Constants.Companion.LAST_RECIPE
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.data.storage.models.states.SuccessResult
import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.domain.usecase.database.GetImageFromDBUseCase
import com.example.smoothie.domain.usecase.database.GetRandomRecipeFromDbUseCase
import com.example.smoothie.domain.usecase.sharedpref.GetRecipeFromSharPrefUseCase
import com.example.smoothie.domain.usecase.sharedpref.SaveRecipeSharPrefUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SharedHomeViewModel @Inject constructor(
    private val getRandomRecipeFromDbUseCase: GetRandomRecipeFromDbUseCase,
    private val getImageFromDBUseCase: GetImageFromDBUseCase,
    private val saveRecipeSharPrefUseCase: SaveRecipeSharPrefUseCase,
    private val getRecipeFromSharPrefUseCase: GetRecipeFromSharPrefUseCase
) : BaseViewModel() {

    private var _recipeMutableLiveData = MutableLiveData<Pair<IRecipeModel, Int>>()
    val resultRecipeLiveData: LiveData<Pair<IRecipeModel, Int>> = _recipeMutableLiveData

    private val _loadResultMutableLiveData =
        MutableLiveResult<IRecipeModel>(SuccessResult(RecipeEntity()))
    val loadResultLiveData: LiveResult<IRecipeModel> = _loadResultMutableLiveData

    private val _loadImageMutableLiveData = MutableLiveResult(SuccessResult(Pair(ByteArray(0), -1)))
    val loadImageLiveData: LiveResult<Pair<ByteArray, Int>> = _loadImageMutableLiveData

    fun nextRecipe(pos: Int) = into(_loadResultMutableLiveData) {
        val result = withContext(Dispatchers.IO) {
            return@withContext getRandomRecipeFromDbUseCase.execute()
        }
        _recipeMutableLiveData.value = Pair(result, pos)
        saveRecipeSharPrefUseCase.execute(result, LAST_RECIPE)
        return@into result
    }

    fun loadLastRecipe(indexPage: Int) {
        val displayRecipe: IRecipeModel = getRecipeFromSharPrefUseCase.execute(LAST_RECIPE)
            ?: return
        _recipeMutableLiveData.value = Pair(displayRecipe, indexPage)
    }

    fun getImage(url: String, indexPager: Int) {
        if (url.isBlank()) {
            _loadImageMutableLiveData.value = SuccessResult(Pair(ByteArray(0), -1))
            return
        }
        into(_loadImageMutableLiveData) {
            val result = withContext(Dispatchers.IO) {
                return@withContext getImageFromDBUseCase.execute(url)
            }
            Pair(result, indexPager)
        }
    }

    fun tryAgain(indexPager: Int) {
        nextRecipe(indexPager)
    }

}