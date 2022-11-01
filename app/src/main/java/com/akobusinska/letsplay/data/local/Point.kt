package com.akobusinska.letsplay.data.local

import android.os.Parcelable
import com.akobusinska.letsplay.data.entities.Player
import kotlinx.parcelize.Parcelize

@Parcelize
data class Point(
    var player: Player,
    var points: Int
) : Parcelable
