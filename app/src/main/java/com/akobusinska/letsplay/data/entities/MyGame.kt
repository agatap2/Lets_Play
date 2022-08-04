package com.akobusinska.letsplay.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
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
    var thumbURL: String,
    var gameType: GameType
): Parcelable

enum class GameType(type: String) {
    GAME("game"), EXPANSION("expansion")
}