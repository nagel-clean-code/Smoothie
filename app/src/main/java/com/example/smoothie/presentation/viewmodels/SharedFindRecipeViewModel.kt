package com.example.smoothie.presentation.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.smoothie.R
import com.example.smoothie.data.repository.RecipesPageSource
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.domain.usecase.database.DeleteRecipeInDbUseCase
import com.example.smoothie.domain.usecase.database.GetListRecipeFromDBUseCase
import com.example.smoothie.domain.usecase.database.SaveFavoriteFlagInDbUseCase
import com.example.smoothie.presentation.adapters.RecipeAdapter
import com.example.smoothie.utils.MutableLiveEvent
import com.example.smoothie.utils.publishEvent
import com.example.smoothie.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SharedFindRecipeViewModel @Inject constructor(
    private val getListRecipeFromDBUseCase: GetListRecipeFromDBUseCase,
    private val saveFavoriteFlagInDbUseCase: SaveFavoriteFlagInDbUseCase,
    private val deleteRecipeInDbUseCase: DeleteRecipeInDbUseCase
) : BaseViewModel(), RecipeAdapter.Listener {

    val recipesFlow: Flow<PagingData<RecipeEntity>>

    private val searchBy = MutableLiveData("")

    private val localChanges = LocalChanges()
    private val localChangesFlow = MutableStateFlow(OnChange(localChanges))

    private val _errorEvents = MutableLiveEvent<Int>()
    val errorEvents = _errorEvents.share()

    private var _invalidateEvents = MutableLiveEvent<Unit>()
    val invalidateEvents = _invalidateEvents.share()

    private val _scrollEvents = MutableLiveEvent<Unit>()
    val scrollEvents = _scrollEvents.share()

    val chooseElement = MutableLiveData<RecipeEntity>()

    init {
        val originalRecipesFlow = searchBy.asFlow()
            .debounce(500)
            .flatMapLatest { getPager() }
            .cachedIn(viewModelScope)

        recipesFlow = combine(
            originalRecipesFlow,
            localChangesFlow.debounce(50),
            this::merge
        )
    }

    fun setSearchBy(value: String) {
        if (this.searchBy.value == value) return
        this.searchBy.value = value
        scrollListToTop()
    }

    private fun scrollListToTop() {
        _scrollEvents.publishEvent(Unit)
    }

    private fun getPager(): Flow<PagingData<RecipeEntity>> {
        return Pager(
            PagingConfig(PAGE_SIZE),
            pagingSourceFactory = {
                RecipesPageSource(getListRecipeFromDBUseCase::execute, PAGE_SIZE, searchBy.value)
            }
        ).flow
    }

    fun refresh() {
        searchBy.postValue(searchBy.value)
    }

    override fun onRecipeDelete(recipeEntity: RecipeEntity) {
        if (isInProgress(recipeEntity)) return
        setProgress(recipeEntity, true)
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.w(TAG, throwable)
            showError(R.string.error_delete)
            setProgress(recipeEntity, false)
        }
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.IO) {
                delete(recipeEntity)
                setProgress(recipeEntity, false)
            }
        }
    }

    override fun onToggleFavoriteFlag(recipeEntity: RecipeEntity) {
        if (isInProgress(recipeEntity)) return
        viewModelScope.launch {
            try {
                setProgress(recipeEntity, true)
                setFavoriteFlag(recipeEntity)
            } catch (e: Exception) {
                showError(R.string.error_change_favorite)
            } finally {
                setProgress(recipeEntity, false)
            }
        }
    }

    override fun displayChooseElement(recipeEntity: RecipeEntity) {
        chooseElement.value = recipeEntity
    }

    private fun showError(@StringRes errorMessage: Int) {
        _errorEvents.publishEvent(errorMessage)
    }

    private suspend fun setFavoriteFlag(recipeEntity: RecipeEntity) {
        val newFlagValue = !recipeEntity.isFavorite
        saveFavoriteFlagInDbUseCase.execute(recipeEntity.idRecipe, newFlagValue)
        localChanges.favoriteFlags[recipeEntity.idRecipe] = newFlagValue
        localChangesFlow.value = OnChange(localChanges)
    }

    private suspend fun delete(recipeEntity: RecipeEntity) {
        deleteRecipeInDbUseCase.execute(recipeEntity.idRecipe)
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                invalidateList()
            }
        }
    }

    private fun isInProgress(recipeEntity: RecipeEntity) =
        localChanges.idsInProgress.contains(recipeEntity.idRecipe)

    private fun setProgress(recipeEntity: RecipeEntity, inProgress: Boolean) {
        if (inProgress) {
            localChanges.idsInProgress.add(recipeEntity.idRecipe)
        } else {
            localChanges.idsInProgress.remove(recipeEntity.idRecipe)
        }
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun invalidateList() {
        _invalidateEvents.publishEvent(Unit)
    }

    private fun merge(
        recipes: PagingData<RecipeEntity>,
        localChanges: OnChange<LocalChanges>
    ): PagingData<RecipeEntity> {
        return recipes
            .map { recipe ->
                val isInProgress = localChanges.value.idsInProgress.contains(recipe.idRecipe)
                val localFavoriteFlag = localChanges.value.favoriteFlags[recipe.idRecipe]

                return@map if (localFavoriteFlag == null) {
                    recipe.copy(inProgress = isInProgress)
                } else {
                    recipe.copy(isFavorite = localFavoriteFlag, inProgress = isInProgress)
                }
            }
    }

    /**
     * Non-data class which allows passing the same reference to the
     * MutableStateFlow multiple times in a row.
     */
    class OnChange<T>(val value: T)

    /** (Сквозной кэш)
     * Contains:
     * 1) identifiers of items which are processed now (deleting or favorite
     * flag updating).
     * 2) local favorite flag updates to avoid list reloading
     */
    class LocalChanges {
        val idsInProgress = mutableSetOf<Int>()
        val favoriteFlags = mutableMapOf<Int, Boolean>()
    }

    companion object {
        const val PAGE_SIZE = 9
    }
}