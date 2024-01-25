package com.akobusinska.letsplay.data.entities

import androidx.room.Entity

@Entity(primaryKeys = ["playId", "playerId"], tableName = "play_with_plays_table")
data class PlayWithPlaysCrossRef(
    val playId: Int,
    val playerId: Int
)


