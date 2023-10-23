package com.example.smoothie.di

import android.content.Context
import com.example.smoothie.data.repository.RecipeRepositoryImpl
import com.example.smoothie.data.repository.SessionRepositoryImpl
import com.example.smoothie.data.storage.databases.external.RecipeStorageFBImpl
import com.example.smoothie.data.storage.databases.external.RecipeStorageFB
import com.example.smoothie.data.storage.databases.external.SessionStorageFB
import com.example.smoothie.data.storage.databases.external.SessionStorageFbImpl
import com.example.smoothie.data.storage.sharedprefs.RecipeStorageSharPref
import com.example.smoothie.data.storage.sharedprefs.SessionStorageSharPref
import com.example.smoothie.data.storage.sharedprefs.SessionStorageSharPrefImpl
import com.example.smoothie.data.storage.sharedprefs.SharedPrefRecipeStorageImpl
import com.example.smoothie.domain.repository.RecipeRepository
import com.example.smoothie.domain.repository.SessionRepository
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
    fun provideFirebaseRecipeStorage(sessionStorageSharPref: SessionStorageSharPref): RecipeStorageFB {
        return RecipeStorageFBImpl(sessionStorageSharPref::getUserName)
    }

    @Provides
    @Singleton
    fun provideSessionStorageDb(): SessionStorageFB {
        return SessionStorageFbImpl()
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(
        recipeStorage: RecipeStorageFB,
        sharedPrefRecipeStorage: RecipeStorageSharPref
    ): RecipeRepository {
        return RecipeRepositoryImpl(
            sharedPrefRecipeStorage = sharedPrefRecipeStorage,
            recipeStorage = recipeStorage
        )
    }

    @Provides
    @Singleton
    fun provideSessionStorageSharPref(@ApplicationContext context: Context): SessionStorageSharPref {
        return SessionStorageSharPrefImpl(context = context)
    }

    @Provides
    @Singleton
    fun provideSessionRepository(
        sessionStorageSharPref: SessionStorageSharPref,
        sessionStorageDb: SessionStorageFB
    ): SessionRepository {
        return SessionRepositoryImpl(
            sessionStorageSharPref = sessionStorageSharPref,
            sessionStorageDb = sessionStorageDb
        )
    }
}