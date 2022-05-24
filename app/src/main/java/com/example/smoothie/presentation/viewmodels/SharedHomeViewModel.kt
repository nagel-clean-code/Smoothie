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

    private var _recipeMutableLiveData = MutableLiveData<IRecipeModel>()
    val resultRecipeLiveData: LiveData<IRecipeModel> = _recipeMutableLiveData

    private val _loadResultMutableLiveData = MutableLiveResult<IRecipeModel>(SuccessResult(RecipeEntity()))
    val loadResultLiveData: LiveResult<IRecipeModel> = _loadResultMutableLiveData

    private var _imageLiveDataMutable = MutableLiveData<Drawable?>()
    val resultImageLiveDataMutable: MutableLiveData<Drawable?> = _imageLiveDataMutable

    fun nextRecipe() = into(_loadResultMutableLiveData){
        val result = withContext(Dispatchers.IO) {
                return@withContext getRandomRecipeFromDbUseCase.execute()
        }
        _recipeMutableLiveData.value = result
        return@into result
    }

    fun getImage(url: String, convertFromStringToImage: (String)->Drawable){
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                return@withContext getImageFromDBUseCase.execute(url)
            }
            _imageLiveDataMutable.value = convertFromStringToImage(Base64.encodeToString(result, Base64.DEFAULT))
        }
    }

    fun tryAgain() {
        nextRecipe()
    }

}