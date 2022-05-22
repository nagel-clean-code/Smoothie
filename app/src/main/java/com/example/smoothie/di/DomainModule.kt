package com.example.smoothie.di

import com.example.smoothie.domain.repository.RecipeRepository
import com.example.smoothie.domain.usecase.database.GetRandomRecipeFromDbUseCase
import com.example.smoothie.domain.usecase.database.SaveImageInDBUseCase
import com.example.smoothie.domain.usecase.sharedpref.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule{

    @Provides
    fun provideSaveIngredients(recipeRepository: RecipeRepository) = SaveIngredientsInSharPrefUseCase(recipeRepository)

    @Provides
    fun provideGetIngredients(recipeRepository: RecipeRepository) = GetIngredientsFromSharPrefUseCase(recipeRepository)

    @Provides
    fun provideSaveNameRecipe(recipeRepository: RecipeRepository) = SaveNameRecipeInSharPrefUseCase(recipeRepository)

    @Provides
    fun provideGetNameRecipe(recipeRepository: RecipeRepository) = GetNameRecipeFromSharPrefUseCase(recipeRepository)

    @Provides
    fun provideSaveRecipeToDb(recipeRepository: RecipeRepository) = SaveRecipeToDbUseCase(recipeRepository)

    @Provides
    fun provideGetRandomRecipeFromDb(recipeRepository: RecipeRepository) = GetRandomRecipeFromDbUseCase(recipeRepository)

    @Provides
    fun provideSaveImageSharPrefUseCase(recipeRepository: RecipeRepository) = SaveImageFromAddFormSharPrefUseCase(recipeRepository)

    @Provides
    fun provideSaveImageInDBUseCase(recipeRepository: RecipeRepository) = SaveImageInDBUseCase(recipeRepository)

    @Provides
    fun provideGetImageFromAddFormSharPrefUseCase(recipeRepository: RecipeRepository) = GetImageFromAddFormSharPrefUseCase(recipeRepository)

}