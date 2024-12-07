package com.akobusinska.letsplay.data.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.akobusinska.letsplay.data.entities.CollectionOwnerGameCrossRef
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame
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
    private val collectionOwnerWithGamesDao: CollectionOwnerWithGamesDao
) {

    enum class RequestStatus { LOADING, ERROR, DONE }

    fun getFullCrossRefCollection() = collectionOwnerWithGamesDao.getCollectionOwnersWithGames()

    fun getGameById(id: Int) = localDataSource.getGame(id)

    private fun getGameByBggId(bggId: Int) = localDataSource.getGameIdByBggId(bggId)

    @Insert
    suspend fun insertGame(game: MyGame) = localDataSource.insertGame(game)

    @Update
    suspend fun updateGame(game: MyGame) = localDataSource.updateGame(game)

    @Delete
    fun deleteGameFromDefaultCollection(gameId: Long) = collectionOwnerWithGamesDao.deleteGameFromUsersCollection(gameId, 1)

    suspend fun downloadGamesList(name: String): List<*> {
        return withContext(Dispatchers.IO) {
            remoteDataSource.searchForGames(name)
        }
    }

    suspend fun downloadGamesWithDetailsList(
        userId: Long,
        gamesIDs: List<BoardGamesSearchResult>,
        saveAll: Boolean, clearFirst: Boolean = false
    ): List<MyGame> {
        val chunkedListOfGameIds = gamesIDs.chunked(5)
        val finalList = mutableListOf<MyGame>()

        withContext(Dispatchers.IO) {
            for (list in chunkedListOfGameIds) {
                finalList.addAll(formatData(remoteDataSource.searchForGameDetails(list.map { game -> game.objectId }) as List<BoardGame>))
            }

            if (clearFirst) collectionOwnerWithGamesDao.deleteUsersCollection(userId)

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
        }

        return finalList
    }

    suspend fun addGameToUserCollection(userId: Long, game: MyGame) {
        withContext(Dispatchers.IO) {
            val gameId = getGameByBggId(game.bggId) ?: insertGame(game)
            collectionOwnerWithGamesDao.insertCollectionOwnerWithGames(
                CollectionOwnerGameCrossRef(
                    userId,
                    gameId
                )
            )
        }
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
            if(newGame.maxPlaytime < newGame.minPlaytime) newGame.maxPlaytime = newGame.minPlaytime

            listOfGames.add(newGame)
        }

        listOfGames.forEach { game ->
            if (game.gameType == GameType.GAME && game.expansions.isNotEmpty()) {
                val iterator = game.expansions.iterator()
                while (iterator.hasNext()) {
                    val expansionId = iterator.next()
                    val expansion =
                        listOfGames.find { it.bggId.toLong() == expansionId }
                    if (expansion == null || expansion.gameType == GameType.GAME) {
                        iterator.remove()
                    } else {
                        listOfGames.filter { it.bggId.toLong() == expansionId }[0].parentGame = game.bggId.toLong()
                    }
                }
            } else if (game.gameType == GameType.EXPANSION) game.expansions.clear()
        }

        return listOfGames
    }
}