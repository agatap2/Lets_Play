package com.akobusinska.letsplay.data.repository

import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.local.Filter
import com.akobusinska.letsplay.data.local.GameDao
import com.akobusinska.letsplay.data.remote.GameRemoteDataSource
import com.akobusinska.letsplay.data.xml.BoardGame
import com.akobusinska.letsplay.data.xml.BoardGamesSearchResult
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

    suspend fun downloadGamesList(name: String): List<*> {
        return withContext(Dispatchers.IO) {
            remoteDataSource.searchForGames(name)
        }
    }

    suspend fun downloadGamesWithDetailsList(gamesIDs: List<BoardGamesSearchResult>): List<MyGame> {
        return withContext(Dispatchers.IO) {
            formatData(remoteDataSource.searchForGameDetails(gamesIDs.map { game -> game.objectId }) as List<BoardGame>)
        }
    }

    suspend fun downloadUserCollection(userName: String): List<*> {
        return withContext(Dispatchers.IO) {
            remoteDataSource.searchForUserCollection(userName)
        }
    }

    private fun formatData(games: List<BoardGame>): List<MyGame> {

        val listOfGames = mutableListOf<MyGame>()

        games.forEach { game ->
            val newGame = MyGame(
                game_id = game.id,
                name = game.name ?: "",
                minPlayers = if (game.minPlayers == null || game.minPlayers < 1) 1
                else game.minPlayers,
                maxPlayers = if (game.maxPlayers == null || game.maxPlayers < 1) 20
                else game.maxPlayers,
                minPlaytime = if(game.minPlayTime == null || game.minPlayTime < 5) 5
                else game.minPlayTime.roundDown(),
                maxPlaytime = if(game.maxPlayTime == null || game.maxPlayTime < 5) 120
                else game.maxPlayTime.roundUp(),
                minAge = if (game.age != null) {
                    if (game.age > 18) 18 else if (game.age < 3) 3 else game.age
                } else 3,
                thumbURL = game.image ?: "",
                gameType = if (game.category?.contains(
                        "Expansion for Base-game",
                        true
                    ) == true
                ) GameType.EXPANSION else GameType.GAME,
                expansions = (game.expansions as MutableList<Int>)
            )

            listOfGames.add(newGame)
        }

        return listOfGames
    }
}