package com.akobusinska.letsplay.data.repository

import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.json.GamesList
import com.akobusinska.letsplay.data.local.Filter
import com.akobusinska.letsplay.data.local.GameDao
import com.akobusinska.letsplay.data.remote.GameRemoteDataSource
import com.akobusinska.letsplay.utils.roundDown
import com.akobusinska.letsplay.utils.roundUp
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

    fun getExpansionsListById(id: Int) = localDataSource.getExpansions(id)

    fun getFilteredGames(filter: Filter) = localDataSource.getFilteredGamesCollection(
        filter.numberOfPlayers,
        filter.maxPlaytime,
        filter.age,
        filter.excludeRecommendedForMore
    )

    suspend fun insertGame(game: MyGame) = localDataSource.insertGame(game)

    suspend fun updateGame(game: MyGame) = localDataSource.updateGame(game)

    suspend fun deleteGame(game: MyGame) = localDataSource.deleteGame(game)

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
                minPlaytime = game.minPlaytime?.roundDown() ?: 5,
                maxPlaytime = game.maxPlaytime?.roundUp() ?: 120,
                minAge = if (game.minAge != null) {
                    if (game.minAge > 18) 18 else if (game.minAge < 3) 3 else game.minAge
                } else 3,
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