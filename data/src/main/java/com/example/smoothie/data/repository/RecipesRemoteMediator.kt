package com.example.smoothie.data.repository

import android.util.Log
import androidx.paging.*
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.domain.models.IRecipeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@OptIn(ExperimentalPagingApi::class)
class RecipesRemoteMediator(
    //источник данных локального хранилища
    //Репозиторий для запросов в сеть
) : RemoteMediator<Int, RecipeEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, RecipeEntity>): MediatorResult {
        TODO("Not yet implemented")
    }
}