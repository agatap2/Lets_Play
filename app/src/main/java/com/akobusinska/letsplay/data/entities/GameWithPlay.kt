package com.akobusinska.letsplay.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class GameWithPlay(
    @Embedded var game: MyGame,
    @Relation(
        parentColumn = "game_id",
        entityColumn = "play_id"
    )
    var play: List<Play>
)
