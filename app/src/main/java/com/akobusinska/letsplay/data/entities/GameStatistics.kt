package com.akobusinska.letsplay.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class GameStatistics(
    @Embedded var game: MyGame,
    @Relation(
        entity = Play::class,
        parentColumn = "gameId",
        entityColumn = "foreignGameId"
    )
    var plays: List<PlayWithPlayers>
)
