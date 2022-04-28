package com.example.smoothie.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel

class AddRecipeViewModel: ViewModel() {
    init{
       Log.e("AAA","VM created")
    }

    override fun onCleared() {
        super.onCleared()

    }
}