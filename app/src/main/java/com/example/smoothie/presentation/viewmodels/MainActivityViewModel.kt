package com.example.smoothie.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smoothie.domain.usecase.database.CreateNewAccountDbUseCase
import com.example.smoothie.domain.usecase.sharedpref.sesion.GetUserNameFromSharPrefUseCase
import com.example.smoothie.domain.usecase.sharedpref.sesion.SaveUserNameSharPrefUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getUserNameFromSharPrefUseCase: GetUserNameFromSharPrefUseCase,
    private val saveUserNameSharPrefUseCase: SaveUserNameSharPrefUseCase,
    private val createNewAccountDbUseCase: CreateNewAccountDbUseCase
) : ViewModel() {

    fun getUserName() = getUserNameFromSharPrefUseCase.execute()
    fun setupNewUserName() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val name = createNewAccountDbUseCase.execute()
                saveUserNameSharPrefUseCase.execute(name)
            }
        }
    }
}