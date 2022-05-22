package com.example.smoothie.presentation

import android.view.View
import androidx.fragment.app.Fragment
import com.example.smoothie.data.storage.models.states.ErrorResult
import com.example.smoothie.data.storage.models.states.PendingResult
import com.example.smoothie.data.storage.models.states.Result
import com.example.smoothie.data.storage.models.states.SuccessResult
import com.example.smoothie.presentation.viewmodels.BaseViewModel

abstract class BaseFragment : Fragment() {
    protected abstract val viewModel: BaseViewModel

    fun <T> renderResult(
        root: View, result: Result<T>,
        onPending: () -> Unit,
        onError: (Exception) -> Unit,
        onSuccessResult: (T) -> Unit
    ) {
        when(result){
            is SuccessResult -> onSuccessResult(result.data)
            is PendingResult -> onPending()
            is ErrorResult -> onError(result.exception)
        }
    }
}