package com.akobusinska.letsplay.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "my_game_table", indices = [Index(value = ["bggId"], unique = true)])
data class MyGame(
    @PrimaryKey(autoGenerate = true)
    var gameId: Long = 0L,
    var bggId: Int = -1,
    var name: String = "",
    var minPlayers: Int = 1,
    var maxPlayers: Int = 20,
    var recommendedForMorePlayers: Boolean = false,
    var minPlaytime: Int = 5,
    var maxPlaytime: Int = 120,
    var minAge: Int = 3,
    var thumbURL: String = "",
    var gameType: GameType = GameType.GAME,
    var parentGame: Long = 0,
    @Ignore var expansions: MutableList<Long> = mutableListOf()
) : Parcelable

enum class GameType {
    GAME, EXPANSION
}