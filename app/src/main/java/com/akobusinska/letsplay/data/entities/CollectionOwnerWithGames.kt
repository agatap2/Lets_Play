package com.akobusinska.letsplay.data.entities

import androidx.room.Entity

@Entity(primaryKeys = ["collectionOwnerId", "gameId"])
class CollectionOwnerWithGames(
    val collectionOwnerId: Int,
    val gameId: Int
)