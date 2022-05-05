package com.example.smoothie.di

import android.content.Context
import com.example.smoothie.data.repository.RecipeRepositoryImpl
import com.example.smoothie.data.storage.RecipeStorage
import com.example.smoothie.data.storage.sharedprefs.SharedPrefRecipeStorageImpl
import com.example.smoothie.domain.repository.RecipeRepository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule{

    @Provides
    @Singleton
    fun provideRecipeStorage(@ApplicationContext context: Context): RecipeStorage{
        return SharedPrefRecipeStorageImpl(context = context)
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(recipeStorage: RecipeStorage): RecipeRepository {
        return RecipeRepositoryImpl(recipeStorage)
    }
}