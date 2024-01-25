package com.akobusinska.letsplay.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.akobusinska.letsplay.data.entities.CollectionOwner
import com.akobusinska.letsplay.data.entities.CollectionOwnerWithGames

@Dao
interface CollectionOwnerWithGamesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionOwnerWithGames(join: CollectionOwnerWithGames)

    @Update
    suspend fun updateCollectionOwnerWithGames(join: CollectionOwnerWithGames)

    @Transaction
    @Query("SELECT * FROM collection_owner_table WHERE name = :collectionOwnerName")
    fun getCollectionOwnerWithGames(collectionOwnerName: String): LiveData<List<CollectionOwnerGamePair>>

    @Transaction
    @Query("SELECT * FROM collection_owner_table")
    fun getCollectionOwners(): LiveData<List<CollectionOwnerGamePair>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionOwner(collectionOwner: CollectionOwner): Long

    @Update
    suspend fun updateCollectionOwner(collectionOwner: CollectionOwner)

    @Delete
    suspend fun deleteCollectionOwner(collectionOwner: CollectionOwner)
}