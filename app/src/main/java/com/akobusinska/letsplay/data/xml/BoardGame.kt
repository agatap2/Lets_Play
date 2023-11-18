package com.akobusinska.letsplay.data.xml

data class BoardGame(
    val minPlayers: String?,
    val maxPlayers: String?,
    val minPlayTime: String?,
    val maxPlayTime: String?,
    val age: String?,
    val name: String?,
    val description: String?,
    val image: String?
)