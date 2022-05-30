package com.example.smoothie.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.domain.repository.RecipeRepository

class RecipesPageSource(
    private val repositoryAPI: RecipeRepository
) : PagingSource<Int, RecipeEntity>() {

    override fun getRefreshKey(state: PagingState<Int, RecipeEntity>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecipeEntity> {
        val page = params.key ?: 0
        return try {
            val pageSize = params.loadSize.coerceAtMost(MAX_PAGE_SIZE)

            @Suppress("UNCHECKED_CAST")
            val listRecipe: List<RecipeEntity> =
                repositoryAPI.getListRecipe(page, pageSize) as List<RecipeEntity>

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

    private companion object{
        const val MAX_PAGE_SIZE = 11
    }
}