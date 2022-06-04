package com.example.smoothie.presentation.viewmodels

import android.graphics.drawable.Drawable
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.domain.usecase.database.GetRandomRecipeFromDbUseCase
import com.example.smoothie.data.storage.models.states.SuccessResult
import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.domain.usecase.database.GetImageFromDBUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class SharedHomeViewModel @Inject constructor(
    private val getRandomRecipeFromDbUseCase: GetRandomRecipeFromDbUseCase,
    private val getImageFromDBUseCase: GetImageFromDBUseCase
) : BaseViewModel() {

    private var _recipeMutableLiveData = MutableLiveData<Pair<IRecipeModel, Int>>()
    val resultRecipeLiveData: LiveData<Pair<IRecipeModel, Int>> = _recipeMutableLiveData

    private val _loadResultMutableLiveData =
        MutableLiveResult<IRecipeModel>(SuccessResult(RecipeEntity()))
    val loadResultLiveData: LiveResult<IRecipeModel> = _loadResultMutableLiveData

    private var _imageLiveDataMutable = MutableLiveData<Pair<Drawable?, Int>>()
    val resultImageLiveDataMutable: MutableLiveData<Pair<Drawable?, Int>> = _imageLiveDataMutable

    fun nextRecipe(pos: Int) = into(_loadResultMutableLiveData) {
        val result = withContext(Dispatchers.IO) {
            return@withContext getRandomRecipeFromDbUseCase.execute()
        }
        _recipeMutableLiveData.value = Pair(result, pos)
        return@into result
    }

    fun getImage(url: String, indexPager: Int, convertFromStringToImage: (String) -> Drawable) {
        if (url.isBlank()) {
            _imageLiveDataMutable.value = Pair(null, indexPager)
            return
        }
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                return@withContext getImageFromDBUseCase.execute(url)
            }
            _imageLiveDataMutable.value = Pair(
                convertFromStringToImage(Base64.encodeToString(result, Base64.DEFAULT)),
                indexPager
            )
        }
    }

    fun tryAgain(indexPager: Int) {
        nextRecipe(indexPager)
    }

}