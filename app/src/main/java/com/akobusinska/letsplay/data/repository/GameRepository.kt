package com.akobusinska.letsplay.data.repository

import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.json.GamesList

class GameRepository {

    private fun formatData(data: GamesList): List<MyGame> {

        val listOfGames = mutableListOf<MyGame>()

        data.games?.forEach { game ->
            val newGame = MyGame(
                name = game.name ?: "",
                minPlayers = game.minPlayers ?: 1,
                maxPlayers = game.maxPlayers ?: 100,
                recommendedForMorePlayers = false,
                minPlaytime = game.minPlaytime ?: 0,
                maxPlaytime = game.maxPlaytime ?: 480,
                minAge = game.minAge ?: 8,
                thumbURL = game.thumbUrl ?: game.imageUrl ?: ""
            )

            listOfGames.add(newGame)
        }

        return listOfGames
    }
}