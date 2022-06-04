package com.example.smoothie.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.domain.models.IRecipeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias RecipesPageLoader = suspend (search: String,start: Int, count: Int) -> List<IRecipeModel>

class RecipesPageSource(
    private val recipesPageLoader: RecipesPageLoader,
    private val pageSize: Int,
    private val searchBy: String?
) : PagingSource<Int, RecipeEntity>() {

    override fun getRefreshKey(state: PagingState<Int, RecipeEntity>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecipeEntity> {
        val page = params.key ?: 0
        return try {
            val listRecipe: List<RecipeEntity> = withContext(Dispatchers.IO) {
                @Suppress("UNCHECKED_CAST")
                    return@withContext searchBy?.let {
                        recipesPageLoader.invoke(it, page * pageSize + 1, params.loadSize)
                    } as List<RecipeEntity>
            }
            return LoadResult.Page(
                data = listRecipe,
                prevKey =  if (page == 0) null else page - 1,

                //Из за того, что изначальная страница может быть в три раза больше, такая мудрёная логика
                nextKey = if (listRecipe.size == params.loadSize) page + (params.loadSize / pageSize) else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}