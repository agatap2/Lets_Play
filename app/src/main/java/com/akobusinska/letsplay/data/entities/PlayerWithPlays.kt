package com.akobusinska.letsplay.data.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlayerWithPlays(
    @Embedded var player: Player,
    @Relation(
        parentColumn = "playerId",
        entityColumn = "playId",
        associateBy = Junction(PlayWithPlaysCrossRef::class)
    )
    var plays: List<Play>
)
