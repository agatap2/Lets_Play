package com.akobusinska.letsplay.data.repository

import androidx.lifecycle.map
import com.akobusinska.letsplay.data.entities.CollectionOwner
import com.akobusinska.letsplay.data.entities.CollectionOwnerWithGames
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.local.CollectionOwnerWithGamesDao
import com.akobusinska.letsplay.data.remote.GameRemoteDataSource
import com.akobusinska.letsplay.data.xml.BoardGamesSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CollectionOwnerRepository(
    private val remoteDataSource: GameRemoteDataSource,
    private val localDataSource: CollectionOwnerWithGamesDao
) {

    suspend fun insertUser(user: CollectionOwner) = localDataSource.insertCollectionOwner(user)

    suspend fun insertUserWithGames(userWithGames: CollectionOwnerWithGames) =
        localDataSource.insertCollectionOwnerWithGames(userWithGames)

    suspend fun updateUser(user: CollectionOwner) = localDataSource.updateCollectionOwner(user)

    suspend fun deleteUser(user: CollectionOwner) = localDataSource.deleteCollectionOwner(user)

    suspend fun downloadCollectionOwner(userName: String): CollectionOwner {
        return withContext(Dispatchers.IO) {
            formatData(
                userName,
                remoteDataSource.searchForUserCollection(userName) as List<BoardGamesSearchResult>
            )
        }
    }

    fun getAllUsers() = localDataSource.getCollectionOwners()

    fun getFullUserCollection(userName: String) =
        localDataSource.getCollectionOwnerWithGames(userName).map { users -> users.map { it.games } }
            .map { games -> games.flatten() }

    fun getGamesUserCollection(userName: String) =
        localDataSource.getCollectionOwnerWithGames(userName).map { users -> users.map { it.games } }
            .map { games -> games.flatten() }.map { it.filter { myGame -> myGame.gameType == GameType.GAME } }

    fun getExpansionsUserCollection(userName: String) =
        localDataSource.getCollectionOwnerWithGames(userName).map { users -> users.map { it.games } }
            .map { games -> games.flatten() }.map { it.filter { myGame -> myGame.gameType == GameType.EXPANSION } }

    private fun formatData(
        userName: String,
        userCollection: List<BoardGamesSearchResult>
    ): CollectionOwner {

        val gameIds: MutableList<String> = mutableListOf()

        userCollection.forEach { gameId ->
            gameIds.add(gameId.objectId)
        }

        val user = CollectionOwner(name = userName, games = gameIds)
        println("ID: " + user.collectionOwnerId)

        return user
    }
}