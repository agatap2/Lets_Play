package com.akobusinska.letsplay.data.remote

class GameRemoteDataSource {

    private val gameService = GameService()

    fun searchForGames(name: String) = gameService.loadGamesListXmlFromNetwork(name)

    fun searchForGameDetails(ids: List<String>) = gameService.loadGameDetailsXmlFromNetwork(ids)
}