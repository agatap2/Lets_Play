package com.akobusinska.letsplay.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "player_table")
data class Player(
    @PrimaryKey(autoGenerate = true)
    val playerId: Int,
    var name: String = ""
) : Parcelable