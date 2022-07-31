package com.akobusinska.letsplay.di

import android.content.Context
import androidx.room.Room
import com.akobusinska.letsplay.data.local.GameDao
import com.akobusinska.letsplay.data.local.GamesDatabase
import com.akobusinska.letsplay.data.remote.GameRemoteDataSource
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
        return Room.databaseBuilder(
            context,
            GamesDatabase::class.java,
            "games_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideGameDao(database: GamesDatabase) = database.gameDao

    @Singleton
    @Provides
    fun provideRepository(
        gameRemoteDataSource: GameRemoteDataSource,
        gameLocalDataSource: GameDao
    ): GameRepository = GameRepository(gameRemoteDataSource, gameLocalDataSource)
}