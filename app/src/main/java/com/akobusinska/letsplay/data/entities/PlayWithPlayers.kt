package com.akobusinska.letsplay.data.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlayWithPlayers(
    @Embedded var play: Play,
    @Relation(
        parentColumn = "playId",
        entityColumn = "playerId",
        associateBy = Junction(PlayWithPlaysCrossRef::class)
    )
    var players: List<Player>
)
