package com.akobusinska.letsplay.data.entities

import androidx.room.Entity

@Entity(primaryKeys = ["play_id", "player_id"], tableName = "play_with_plays_table")
data class PlayWithPlaysCrossRef(
    val play_id: Int,
    val player_id: Int
)


