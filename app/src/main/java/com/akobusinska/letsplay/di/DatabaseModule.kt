package com.akobusinska.letsplay.di

import android.content.Context
import com.akobusinska.letsplay.data.local.CollectionOwnerWithGamesDao
import com.akobusinska.letsplay.data.local.GameDao
import com.akobusinska.letsplay.data.local.GamesDatabase
import com.akobusinska.letsplay.data.remote.GameRemoteDataSource
import com.akobusinska.letsplay.data.repository.CollectionOwnerRepository
import com.akobusinska.letsplay.data.repository.GameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): GamesDatabase {
        return GamesDatabase.getInstance(context)!!
    }

    @Singleton
    @Provides
    fun provideGameDao(database: GamesDatabase) = database.gameDao

    @Singleton
    @Provides
    fun provideCollectionOwnerWithGamesDao(database: GamesDatabase) = database.collectionOwnerWithGamesDao

    @Singleton
    @Provides
    fun provideRepository(
        gameRemoteDataSource: GameRemoteDataSource,
        gameLocalDataSource: GameDao
    ): GameRepository = GameRepository(gameRemoteDataSource, gameLocalDataSource)

    @Singleton
    @Provides
    fun provideUserRepository(
        gameRemoteDataSource: GameRemoteDataSource,
        gameLocalDataSource: CollectionOwnerWithGamesDao
    ): CollectionOwnerRepository = CollectionOwnerRepository(gameRemoteDataSource, gameLocalDataSource)
}