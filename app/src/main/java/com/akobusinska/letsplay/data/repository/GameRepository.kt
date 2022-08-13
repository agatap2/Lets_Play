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

    fun getGameById(id: Int) = localDataSource.getGame(id)

    suspend fun insertGame(game: MyGame) = localDataSource.insertGame(game)

    suspend fun updateGame(game: MyGame) = localDataSource.updateGame(game)

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
                maxPlayers = game.maxPlayers ?: 20,
                minPlaytime = game.minPlaytime ?: 5,
                maxPlaytime = game.maxPlaytime ?: 120,
                minAge = game.minAge ?: 3,
                thumbURL = game.thumbUrl ?: game.imageUrl ?: "",
                gameType = if (game.type == "game" && !game.name?.contains(
                        "expansion",
                        true
                    )!!
                ) GameType.GAME else GameType.EXPANSION,
            )

            listOfGames.add(newGame)
        }

        return listOfGames
    }
}