package com.akobusinska.letsplay.data.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GamesList(
    @SerializedName("games")
    @Expose
    val games: List<Game>?,

    @SerializedName("count")
    @Expose
    val count: Int?
)