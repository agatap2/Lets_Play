package com.akobusinska.letsplay.data.xml

interface IBoardGames

data class BoardGamesSearchResult(
    val objectId: String,
) : IBoardGames

data class BoardGame(
    val id: Int,
    val minPlayers: Int?,
    val maxPlayers: Int?,
    val minPlayTime: Int?,
    val maxPlayTime: Int?,
    val age: Int?,
    val name: String?,
    val description: String?,
    val category: String?,
    val expansions: ArrayList<Int>,
    val image: String?
) : IBoardGames