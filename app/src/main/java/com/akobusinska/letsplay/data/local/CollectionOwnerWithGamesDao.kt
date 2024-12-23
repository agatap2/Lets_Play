package com.akobusinska.letsplay.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.akobusinska.letsplay.data.entities.CollectionOwnerGameCrossRef

@Dao
interface CollectionOwnerWithGamesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionOwnerWithGames(join: CollectionOwnerGameCrossRef)

    @Update
    suspend fun updateCollectionOwnerWithGames(join: CollectionOwnerGameCrossRef)

    @Delete
    suspend fun deleteCollectionOwnerWithGames(join: CollectionOwnerGameCrossRef)

    @Query("DELETE FROM CollectionOwnerGameCrossRef WHERE collectionOwnerId=:userId")
    fun deleteUsersCollection(userId: Long)

    @Query("DELETE FROM CollectionOwnerGameCrossRef WHERE gameId=:gameId and collectionOwnerId=:userId")
    fun deleteGameFromUsersCollection(gameId: Long, userId: Long)

    @Transaction
    @Query("SELECT * FROM collection_owner_table")
    fun getCollectionOwnersWithGames(): LiveData<List<CollectionOwnerWithGames>>
}