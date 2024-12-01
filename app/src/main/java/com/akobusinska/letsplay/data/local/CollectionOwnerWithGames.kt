package com.akobusinska.letsplay.data.local

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.akobusinska.letsplay.data.entities.CollectionOwner
import com.akobusinska.letsplay.data.entities.CollectionOwnerGameCrossRef
import com.akobusinska.letsplay.data.entities.MyGame

data class CollectionOwnerWithGames(
    @Embedded
    var collectionOwner: CollectionOwner,
    @Relation(
        parentColumn = "collectionOwnerId",
        entity = MyGame::class,
        entityColumn = "gameId",
        associateBy = Junction(
            value = CollectionOwnerGameCrossRef::class,
            parentColumn = "collectionOwnerId",
            entityColumn = "gameId"
        )
    )

    var games: List<MyGame>
)