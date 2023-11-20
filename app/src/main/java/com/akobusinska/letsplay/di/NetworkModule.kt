package com.akobusinska.letsplay.di

import com.akobusinska.letsplay.data.remote.GameRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

//    @Provides
//    fun provideGson(): Gson = GsonBuilder().create()
//
//    @Singleton
//    @Provides
//    fun provideRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
//        .baseUrl("https://boardgamegeek.com/xmlapi2/")
//        .addConverterFactory(GsonConverterFactory.create(gson))
//        .build()
//
//    @Provides
//    fun provideGameService(retrofit: Retrofit): GameService =
//        retrofit.create(GameService::class.java)
//
//    @Singleton
//    @Provides
//    fun provideGameRemoteDataSource(gameService: GameService): GameRemoteDataSource =
//        GameRemoteDataSource(gameService)

    @Singleton
    @Provides
    fun provideGameRemoteDataSource(): GameRemoteDataSource =
        GameRemoteDataSource()
}