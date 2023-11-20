package com.akobusinska.letsplay.data.xml

interface IBoardGames {
    val name: String?
}

data class BoardGamesSearchResult(
    val objectId: String,
    override val name: String?
) : IBoardGames

data class BoardGame(
    val minPlayers: Int?,
    val maxPlayers: Int?,
    val minPlayTime: Int?,
    val maxPlayTime: Int?,
    val age: Int?,
    override val name: String?,
    val description: String?,
    val image: String?
) : IBoardGames