package com.akobusinska.letsplay.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "my_game_table")
data class MyGame(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String = "",
    var minPlayers: Int = 1,
    var maxPlayers: Int = 20,
    var recommendedForMorePlayers: Boolean = false,
    var minPlaytime: Int = 5,
    var maxPlaytime: Int = 120,
    var minAge: Int = 3,
    var thumbURL: String = "",
    var gameType: GameType = GameType.GAME,
    var parentGame: Int = id,
    var expansions: MutableList<Int> = mutableListOf()
) : Parcelable

enum class GameType {
    GAME, EXPANSION
}