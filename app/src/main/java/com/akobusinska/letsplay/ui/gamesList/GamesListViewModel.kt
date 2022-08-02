package com.akobusinska.letsplay.ui.gamesList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GamesListViewModel @Inject constructor(private val repository: GameRepository) : ViewModel() {

    private lateinit var _gamesCollection: LiveData<List<MyGame>>
    val gamesCollection: LiveData<List<MyGame>>
        get() = _gamesCollection

    private val _navigateToGameDetails = MutableLiveData<Int>()
    val navigateToGameDetails: LiveData<Int>
        get() = _navigateToGameDetails

    init {
        getGamesCollection(false)
    }

    fun getGamesCollection(
        filtered: Boolean,
        gameType: GameType = GameType.GAME,
        name: String = ""
    ) {

        _gamesCollection = if (filtered) {
            if (gameType == GameType.GAME)
                repository.getOnlyGames()
            else
                repository.getOnlyExpansions()
        } else {
            repository.getFullCollection()
        }

        if (name.isNotBlank()) filterGames(name)
    }

    fun navigateToGameDetails(gameId: Int) {
        _navigateToGameDetails.value = gameId
    }

    fun doneNavigating() {
        _navigateToGameDetails.value = null
    }

    fun filterGames(name: String) {
        Transformations.map(_gamesCollection) { myGames ->
            myGames.filter { game ->
                game.name.contains(name)
            }
        }
    }
}