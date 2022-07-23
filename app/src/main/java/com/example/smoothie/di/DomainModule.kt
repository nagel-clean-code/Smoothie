package com.example.smoothie.di

import com.example.smoothie.domain.repository.RecipeRepository
import com.example.smoothie.domain.repository.SessionRepository
import com.example.smoothie.domain.usecase.database.*
import com.example.smoothie.domain.usecase.sharedpref.recipe.GetImageFromAddFormSharPrefUseCase
import com.example.smoothie.domain.usecase.sharedpref.recipe.GetRecipeFromSharPrefUseCase
import com.example.smoothie.domain.usecase.sharedpref.recipe.SaveImageFromAddFormSharPrefUseCase
import com.example.smoothie.domain.usecase.sharedpref.recipe.SaveRecipeSharPrefUseCase
import com.example.smoothie.domain.usecase.sharedpref.sesion.GetUserNameFromSharPrefUseCase
import com.example.smoothie.domain.usecase.sharedpref.sesion.SaveUserNameSharPrefUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule{

    @Provides
    fun provideSaveRecipeSharPrefUseCase(recipeRepository: RecipeRepository) = SaveRecipeSharPrefUseCase(recipeRepository)

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

    @Provides
    fun provideGetImageFromDBUseCase(recipeRepository: RecipeRepository) = GetImageFromDBUseCase(recipeRepository)

    @Provides
    fun provideGetListRecipeFromDBUseCase(recipeRepository: RecipeRepository) = GetListRecipeFromDBUseCase(recipeRepository)

    @Provides
    fun provideSaveFavoriteFlagInDbUseCase(recipeRepository: RecipeRepository) = SaveFavoriteFlagInDbUseCase(recipeRepository)

    @Provides
    fun provideDeleteRecipeInDbUseCase(recipeRepository: RecipeRepository) = DeleteRecipeInDbUseCase(recipeRepository)

    @Provides
    fun provideGetRecipeFromSharPrefUseCase(recipeRepository: RecipeRepository) = GetRecipeFromSharPrefUseCase(recipeRepository)

    @Provides
    fun provideSaveUserNameSharPrefUseCase(sessionRepository: SessionRepository) = SaveUserNameSharPrefUseCase(sessionRepository)

    @Provides
    fun provideGetUserNameFromSharPrefUseCase(sessionRepository: SessionRepository):GetUserNameFromSharPrefUseCase =
        GetUserNameFromSharPrefUseCase(sessionRepository)

}