package com.gfreeman.takeme.di

import android.content.Context
import androidx.room.Room
import com.gfreeman.takeme.home.model.db.DB_NAME
import com.gfreeman.takeme.home.model.db.FavoritesDao
import com.gfreeman.takeme.home.model.db.FavoritesDatabase
import com.gfreeman.takeme.home.model.favs.FavoriteRepository
import com.gfreeman.takeme.home.model.favs.FavoriteRepositoryImpl
import com.gfreeman.takeme.home.model.profile.ProfileRepository
import com.gfreeman.takeme.home.model.profile.ProfileRepositoryImpl
import com.gfreeman.takeme.login.model.AuthRepository
import com.gfreeman.takeme.login.model.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Dependency Injection Module for Database & Repository dependencies.
 * Installed in SingletonComponent so dependencies live for the entire Application lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides a single instance of Room FavoritesDatabase.
     */
    @Provides
    @Singleton
    fun provideFavoritesDatabase(
        @ApplicationContext context: Context
    ): FavoritesDatabase {
        return Room.databaseBuilder(
            context,
            FavoritesDatabase::class.java,
            DB_NAME
        ).build()
    }

    /**
     * Provides FavoritesDao extracted from FavoritesDatabase.
     */
    @Provides
    @Singleton
    fun provideFavoritesDao(
        database: FavoritesDatabase
    ): FavoritesDao {
        return database.favoritesDao()
    }

    /**
     * Provides FavoriteRepository implementation using the provided FavoritesDao.
     */
    @Provides
    @Singleton
    fun provideFavoriteRepository(
        favoritesDao: FavoritesDao
    ): FavoriteRepository {
        return FavoriteRepositoryImpl(favoritesDao)
    }

    /**
     * Provides ProfileRepository implementation.
     */
    @Provides
    @Singleton
    fun provideProfileRepository(
        @ApplicationContext context: Context
    ): ProfileRepository {
        return ProfileRepositoryImpl(context)
    }

    /**
     * Provides AuthRepository implementation.
     */
    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context
    ): AuthRepository {
        return AuthRepositoryImpl(context)
    }
}
