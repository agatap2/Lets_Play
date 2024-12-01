package com.akobusinska.letsplay.data.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.akobusinska.letsplay.data.entities.CollectionOwnerGameCrossRef
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.local.CollectionOwnerDao
import com.akobusinska.letsplay.data.local.CollectionOwnerWithGamesDao
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
    private val localDataSource: GameDao,
    private val collectionOwnerDao: CollectionOwnerDao,
    private val collectionOwnerWithGamesDao: CollectionOwnerWithGamesDao
) {

    enum class RequestStatus { LOADING, ERROR, DONE }

    fun getFullCollection() = localDataSource.getCollection()

    fun getOnlyGames() = localDataSource.getFilteredCollection(GameType.GAME)

    fun getOnlyExpansions() = localDataSource.getFilteredCollection(GameType.EXPANSION)

    fun getFullCrossRefCollection() = collectionOwnerWithGamesDao.getCollectionOwnersWithGames()

    fun getOnlyGamesOfUser(userName: String) = localDataSource.getFilteredCollection(GameType.GAME)

    fun getOnlyExpansionOfUsers(userName: String) =
        localDataSource.getFilteredCollection(GameType.EXPANSION)

    fun getGameById(id: Long) = localDataSource.getGame(id)

    private fun getGameByBggId(bggId: Int) = localDataSource.getGameWithSpecificBggId(bggId)

    fun getExpansionsListById(id: Long) = localDataSource.getExpansions(id)

    @Insert
    suspend fun insertGame(game: MyGame) = localDataSource.insertGame(game)

    @Update
    suspend fun updateGame(game: MyGame) = localDataSource.updateGame(game)

    @Delete
    suspend fun deleteGame(game: MyGame) = localDataSource.deleteGame(game)

    suspend fun downloadGamesList(name: String): List<*> {
        return withContext(Dispatchers.IO) {
            remoteDataSource.searchForGames(name)
        }
    }

    suspend fun downloadGamesWithDetailsList(
        userId: Long,
        gamesIDs: List<BoardGamesSearchResult>,
        saveAll: Boolean
    ): List<MyGame> {
        val chunkedListOfGameIds = gamesIDs.chunked(5)
        val finalList = mutableListOf<MyGame>()

        for (list in chunkedListOfGameIds) {
            finalList.addAll(formatData(remoteDataSource.searchForGameDetails(list.map { game -> game.objectId }) as List<BoardGame>))
        }

        if (saveAll) {
            for (game in finalList) {
                game.let {
                    withContext(Dispatchers.IO) {
                        val gameId = getGameByBggId(it.bggId) ?: insertGame(it)
                        collectionOwnerWithGamesDao.insertCollectionOwnerWithGames(
                            CollectionOwnerGameCrossRef(
                                userId,
                                gameId
                            )
                        )
                    }
                }
            }
        }

        return finalList
    }

    private fun formatData(games: List<BoardGame>): List<MyGame> {

        val listOfGames = mutableListOf<MyGame>()

        games.forEach { game ->
            val newGame = MyGame(
                bggId = game.id,
                name = game.name ?: "",
                minPlayers = if (game.minPlayers == null || game.minPlayers < 1) 1
                else game.minPlayers,
                maxPlayers = if (game.maxPlayers == null || game.maxPlayers < 1) 20
                else game.maxPlayers,
                minPlaytime = if (game.minPlayTime == null || game.minPlayTime < 5) 5
                else if (game.minPlayTime > 120) 120
                else game.minPlayTime.roundDown(),
                maxPlaytime = if (game.maxPlayTime == null || game.maxPlayTime > 120) 120
                else if (game.maxPlayTime < 5) 5
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
                expansions = (game.expansions as MutableList<Long>)
            )

            listOfGames.add(newGame)
        }

//        listOfGames.forEach { game ->
//            if (game.gameType == GameType.GAME && game.expansions.isNotEmpty()) {
//                val iterator = game.expansions.iterator()
//                while (iterator.hasNext()) {
//                    val expansionId = iterator.next()
//                    val expansion =
//                        listOfGames.find { it.gameId == expansionId }
//                    if (expansion == null || expansion.gameType == GameType.GAME) {
//                        iterator.remove()
//                    } else {
//                        expansion.parentGame = game.parentGame
//                    }
//                }
//            } else if (game.gameType == GameType.EXPANSION) game.expansions.clear()
//        }

        return listOfGames
    }
}