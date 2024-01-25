package com.akobusinska.letsplay.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class GameWithPlays(
    @Embedded var game: MyGame,
    @Relation(
        parentColumn = "gameId",
        entityColumn = "playId"
    )
    var play: List<Play>
)
