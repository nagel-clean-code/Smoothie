package com.example.smoothie.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.domain.usecase.database.GetRandomRecipeFromDbUseCase
import com.example.smoothie.data.storage.models.states.SuccessResult
import com.example.smoothie.domain.models.IRecipeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class SharedHomeViewModel @Inject constructor(
    private val getRandomRecipeFromDbUseCase: GetRandomRecipeFromDbUseCase
) : BaseViewModel() {

    private var _recipeMutableLiveData = MutableLiveData<IRecipeModel>()
    val resultRecipeLiveData: LiveData<IRecipeModel> = _recipeMutableLiveData

    private val _loadResultMutableLiveData = MutableLiveResult<IRecipeModel>(SuccessResult(RecipeEntity()))
    val loadResultLiveData: LiveResult<IRecipeModel> = _loadResultMutableLiveData

    fun nextRecipe() = into(_loadResultMutableLiveData){
        val result = withContext(Dispatchers.IO) {
                return@withContext getRandomRecipeFromDbUseCase.execute()
        }
        _recipeMutableLiveData.value = result
        return@into result
    }

    fun tryAgain() {
        nextRecipe()
    }

}