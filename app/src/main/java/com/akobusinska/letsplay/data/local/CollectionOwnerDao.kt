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

@Dao
interface CollectionOwnerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionOwner(collectionOwner: CollectionOwner): Long

    @Update
    suspend fun updateCollectionOwner(collectionOwner: CollectionOwner)

    @Delete
    suspend fun deleteCollectionOwner(collectionOwner: CollectionOwner)

    @Transaction
    @Query("SELECT * FROM collection_owner_table")
    fun getCollectionOwners(): LiveData<List<CollectionOwner>>

    @Transaction
    @Query("SELECT * FROM collection_owner_table WHERE name = :name LIMIT 1")
    fun getCollectionOwner(name: String): LiveData<CollectionOwner>

    @Transaction
    @Query("SELECT * FROM collection_owner_table ORDER BY collectionOwnerId DESC LIMIT 1")
    fun getLastCollectionOrder(): LiveData<CollectionOwner>
}