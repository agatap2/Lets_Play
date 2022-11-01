package com.akobusinska.letsplay.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akobusinska.letsplay.data.local.Point
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "plays_table")
data class Play(
    @PrimaryKey(autoGenerate = true)
    val play_id: Int,
    var date: Date,
    var winner: Int,
    var points: List<Point>? = listOf(),
    var duration: Int? = 0,
    var foreign_game_id: Int
) : Parcelable
