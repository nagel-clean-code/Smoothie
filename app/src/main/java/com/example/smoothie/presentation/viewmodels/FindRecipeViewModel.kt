package com.example.smoothie.presentation.viewmodels

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.smoothie.data.repository.RecipesPageSource
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class FindRecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : BaseViewModel() {

    val recipes: Flow<PagingData<RecipeEntity>> = Pager(PagingConfig(pageSize = 5),
        pagingSourceFactory = { RecipesPageSource(recipeRepository) }
    ).flow

}