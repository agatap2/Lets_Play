package com.akobusinska.letsplay.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["playId", "playerId"], tableName = "play_with_plays_table")
data class PlayWithPlaysCrossRef(
    val playId: Int,
    @ColumnInfo(index = true)
    val playerId: Int
)


