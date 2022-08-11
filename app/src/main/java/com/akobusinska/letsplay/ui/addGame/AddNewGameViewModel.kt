package com.akobusinska.letsplay.ui.addGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.akobusinska.letsplay.data.entities.MyGame

class AddNewGameViewModel(game: MyGame?) : ViewModel() {

    private val _newGame = MutableLiveData<MyGame>()
    val newGame: LiveData<MyGame>
        get() = _newGame

    init {
        _newGame.value = game
    }

    val minNumberOfPlayers = Transformations.map(newGame) { game ->
        game.minPlayers.toString()
    }

    val maxNumberOfPlayers = Transformations.map(newGame) { game ->
        if (game.maxPlayers > 20)
            "20+"
        else
            game.maxPlayers.toString()
    }

    val minPlaytime = Transformations.map(newGame) { game ->
        game.minPlaytime.toString()
    }

    val maxPlaytime = Transformations.map(newGame) { game ->
        if (game.maxPlaytime > 120)
            "2h+"
        else
            game.maxPlaytime.toString()
    }

    val minAge = Transformations.map(newGame) { game ->
        game.minAge.toString()
    }

    val numberOfPlayersRange = MutableLiveData<List<Float>>().apply {
        value = if (newGame.value?.maxPlayers!! > 20)
            listOf(newGame.value!!.minPlayers.toFloat(), 20F)
        else
            listOf(newGame.value!!.minPlayers.toFloat(), newGame.value!!.maxPlayers.toFloat())
    }

    val playtimeRange = MutableLiveData<List<Float>>().apply {
        value = if (newGame.value?.maxPlaytime!! > 120)
            listOf(newGame.value!!.minPlaytime.toFloat(), 120F)
        else
            listOf(newGame.value?.minPlaytime!!.toFloat(), newGame.value?.maxPlaytime!!.toFloat())
    }

}