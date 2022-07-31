package com.akobusinska.letsplay.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_game_table")
data class MyGame(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String,
    var minPlayers: Int,
    var maxPlayers: Int,
    var recommendedForMorePlayers: Boolean = false,
    var minPlaytime: Int,
    var maxPlaytime: Int,
    var minAge: Int,
    var thumbURL: String
)