package com.example.smoothie.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smoothie.data.storage.models.states.ErrorResult
import com.example.smoothie.data.storage.models.states.PendingResult
import com.example.smoothie.data.storage.models.states.Result
import com.example.smoothie.data.storage.models.states.SuccessResult
import kotlinx.coroutines.launch

typealias LiveResult<T> = LiveData<Result<T>>
typealias MutableLiveResult<T> = MutableLiveData<Result<T>>

open class BaseViewModel : ViewModel() {

    fun onResult(result: Any) {

    }

    fun <T> into(liveResult: MutableLiveResult<T>, block: suspend () -> T) = viewModelScope.launch {
        liveResult.value = PendingResult()
        try {
            liveResult.value = SuccessResult(block())
        } catch (e: Exception) {
            liveResult.value = ErrorResult(e)
        }
    }

}