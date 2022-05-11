package com.example.smoothie.di

import com.example.smoothie.domain.repository.RecipeRepository
import com.example.smoothie.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule{

    @Provides
    fun provideSaveIngredients(recipeRepository: RecipeRepository) = SaveIngredientsUseCase(recipeRepository)

    @Provides
    fun provideGetIngredients(recipeRepository: RecipeRepository) = GetIngredientsUseCase(recipeRepository)

    @Provides
    fun provideSaveNameRecipe(recipeRepository: RecipeRepository) = SaveNameRecipeUseCase(recipeRepository)

    @Provides
    fun provideGetNameRecipe(recipeRepository: RecipeRepository) = GetNameRecipeUseCase(recipeRepository)

    @Provides
    fun provideSaveRecipeToDb(recipeRepository: RecipeRepository) = SaveRecipeToDbUseCase(recipeRepository)

}