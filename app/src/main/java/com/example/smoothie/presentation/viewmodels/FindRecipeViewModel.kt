package com.example.smoothie.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.smoothie.data.repository.RecipesPageSource
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class FindRecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : BaseViewModel() {

    val recipes: Flow<PagingData<RecipeEntity>>

    private val searchBy = MutableLiveData("")

    init {
        recipes = searchBy.asFlow()
            .debounce(500)
            .flatMapLatest { getPager() }
            .cachedIn(viewModelScope)
    }

    private fun getPager(): Flow<PagingData<RecipeEntity>> {
        return Pager(
            PagingConfig(PAGE_SIZE),
            pagingSourceFactory = {
                RecipesPageSource(recipeRepository, PAGE_SIZE, searchBy.value)
            }
        ).flow
    }

    fun refresh() {
        searchBy.postValue(searchBy.value)
    }

    companion object {
        const val PAGE_SIZE = 9
    }
}