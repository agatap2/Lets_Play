package com.akobusinska.letsplay.data.remote

import javax.inject.Inject

class GameRemoteDataSource @Inject constructor(private val gameService: GameService) {

    suspend fun searchForGames(name: String) = gameService.getSearchResult(name = name)
}