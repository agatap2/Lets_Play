package com.akobusinska.letsplay.data.repository

import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.json.GamesList
import com.akobusinska.letsplay.data.local.GameDao
import com.akobusinska.letsplay.data.remote.GameRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository(
    private val remoteDataSource: GameRemoteDataSource,
    private val localDataSource: GameDao
) {

    enum class RequestStatus { LOADING, ERROR, DONE }

    fun getFullCollection() = localDataSource.getCollection()

    fun getOnlyGames() = localDataSource.getFilteredCollection(GameType.GAME)

    fun getOnlyExpansions() = localDataSource.getFilteredCollection(GameType.EXPANSION)

    suspend fun downloadGamesList(name: String): List<MyGame> {
        return withContext(Dispatchers.IO) {
            formatData(remoteDataSource.searchForGames(name))
        }
    }

    private fun formatData(data: GamesList): List<MyGame> {

        val listOfGames = mutableListOf<MyGame>()

        data.games?.forEach { game ->
            val newGame = MyGame(
                name = game.name ?: "",
                minPlayers = game.minPlayers ?: 1,
                maxPlayers = game.maxPlayers ?: 100,
                recommendedForMorePlayers = false,
                minPlaytime = game.minPlaytime ?: 0,
                maxPlaytime = game.maxPlaytime ?: 480,
                minAge = game.minAge ?: 8,
                thumbURL = game.thumbUrl ?: game.imageUrl ?: "",
                gameType = if (game.type == "game") GameType.GAME else GameType.EXPANSION
            )

            listOfGames.add(newGame)
        }

        return listOfGames
    }
}