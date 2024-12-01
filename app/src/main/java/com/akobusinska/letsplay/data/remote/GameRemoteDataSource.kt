package com.akobusinska.letsplay.data.remote

class GameRemoteDataSource {

    private val gameService = GameService()

    suspend fun searchForGames(name: String) = gameService.loadGamesListXmlFromNetwork(name)

    suspend fun searchForGameDetails(ids: List<String>) = gameService.loadGameDetailsXmlFromNetwork(ids)

    suspend fun searchForUserCollection(userName: String) = gameService.loadCollectionXmlFromNetwork(userName)
}