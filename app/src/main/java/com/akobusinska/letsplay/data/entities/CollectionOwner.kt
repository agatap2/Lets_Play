package com.akobusinska.letsplay.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "collection_owner_table")
data class CollectionOwner(
    @PrimaryKey(autoGenerate = true)
    var collectionOwnerId: Long = 0L,
    var name: String = "",
    var customName: String = "",
    @Ignore var games: MutableList<String> = mutableListOf()
) : Parcelable
