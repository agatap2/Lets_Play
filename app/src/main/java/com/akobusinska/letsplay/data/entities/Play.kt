package com.akobusinska.letsplay.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akobusinska.letsplay.data.local.Point
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "plays_table")
data class Play(
    @PrimaryKey(autoGenerate = true)
    val playId: Int,
    var date: Date,
    var winner: Int,
    var points: List<Point>? = listOf(),
    var duration: Int? = 0,
    var foreignGameId: Int
) : Parcelable
