package com.akobusinska.letsplay.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["collectionOwnerId", "gameId"])
data class CollectionOwnerGameCrossRef(
    val collectionOwnerId: Long,
    @ColumnInfo(index = true)
    val gameId: Long
)