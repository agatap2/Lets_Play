package com.akobusinska.letsplay.data.repository

import androidx.lifecycle.map
import com.akobusinska.letsplay.data.entities.CollectionOwner
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.local.CollectionOwnerDao
import com.akobusinska.letsplay.data.local.CollectionOwnerWithGamesDao
import com.akobusinska.letsplay.data.remote.GameRemoteDataSource
import com.akobusinska.letsplay.data.xml.BoardGamesSearchResult

class CollectionOwnerRepository(
    private val remoteDataSource: GameRemoteDataSource,
    private val collectionOwnerDao: CollectionOwnerDao,
    private val collectionOwnerWithGamesDao: CollectionOwnerWithGamesDao
) {

    private suspend fun insertUser(user: CollectionOwner) =
        collectionOwnerDao.insertCollectionOwner(user)

    suspend fun updateUser(user: CollectionOwner) = collectionOwnerDao.updateCollectionOwner(user)

    suspend fun deleteUser(user: CollectionOwner) = collectionOwnerDao.deleteCollectionOwner(user)

    suspend fun downloadCollectionOwner(userName: String): CollectionOwner? {
        val user = formatData(
            userName,
            remoteDataSource.searchForUserCollection(userName) as List<BoardGamesSearchResult>
        )

        if (user != null) {
            user.collectionOwnerId = insertUser(user)
        }

        return user
    }

    suspend fun refreshUsersCollection(userName: String): List<String> {
        val user = formatData(
            userName,
            remoteDataSource.searchForUserCollection(userName) as List<BoardGamesSearchResult>
        )
        return user?.games ?: emptyList()
    }

    fun getAllUsers() = collectionOwnerDao.getCollectionOwners()

    fun getLastUser() = collectionOwnerDao.getLastCollectionOrder()

    fun getUserByName(name: String) = collectionOwnerDao.getCollectionOwner(name)

    fun getGamesUserCollection(userName: String) =
        collectionOwnerWithGamesDao.getCollectionOwnersWithGames()
            .map { it.filter { data -> data.collectionOwner.name == userName } }
            .map { users -> users.map { it.games } }
            .map { games -> games.flatten() }
            .map { it.filter { myGame -> myGame.gameType == GameType.GAME } }

    private fun formatData(
        userName: String,
        userCollection: List<BoardGamesSearchResult>?
    ): CollectionOwner? {

        val gameIds: MutableList<String> = mutableListOf()

        return if (userCollection != null) {
            userCollection.forEach { gameId ->
                gameIds.add(gameId.objectId)
            }

            CollectionOwner(name = userName, games = gameIds, customName = userName)
        } else {
            null
        }
    }
}