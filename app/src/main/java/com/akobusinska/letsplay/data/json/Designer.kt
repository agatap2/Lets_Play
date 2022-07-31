package com.akobusinska.letsplay.data.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Designer(
    @SerializedName("id")
    @Expose
    val id: String?,
    @SerializedName("num_games")
    @Expose
    val numGames: Any?,
    @SerializedName("score")
    @Expose
    val score: Int?,
    @SerializedName("game")
    @Expose
    val game: Game?,
    @SerializedName("url")
    @Expose
    val url: String?,
    @SerializedName("images")
    @Expose
    val images: Images?
)