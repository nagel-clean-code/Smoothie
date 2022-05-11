package com.example.smoothie.di

import android.content.Context
import com.example.smoothie.data.repository.RecipeRepositoryImpl
import com.example.smoothie.data.storage.databases.FirebaseRecipeStorageImpl
import com.example.smoothie.data.storage.databases.RecipeStorageDB
import com.example.smoothie.data.storage.sharedprefs.RecipeStorageSharPref
import com.example.smoothie.data.storage.sharedprefs.SharedPrefRecipeStorageImpl
import com.example.smoothie.domain.repository.RecipeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideSharedPrefRecipeStorage(@ApplicationContext context: Context): RecipeStorageSharPref {
        return SharedPrefRecipeStorageImpl(context = context)
    }

    @Provides
    @Singleton
    fun provideFirebaseRecipeStorage(): RecipeStorageDB {
        return FirebaseRecipeStorageImpl()
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(
        recipeStorage: RecipeStorageDB,
        sharedPrefRecipeStorage: RecipeStorageSharPref
    ): RecipeRepository {
        return RecipeRepositoryImpl(
            sharedPrefRecipeStorage = sharedPrefRecipeStorage,
            recipeStorage = recipeStorage
        )
    }
}