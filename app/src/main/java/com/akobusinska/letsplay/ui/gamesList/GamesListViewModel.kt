package com.akobusinska.letsplay.ui.gamesList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GamesListViewModel @Inject constructor(private val repository: GameRepository) : ViewModel() {

    private lateinit var _fullGamesCollection: List<MyGame>
    private val _gamesCollection = MediatorLiveData<List<MyGame>>()
    val gamesCollection: LiveData<List<MyGame>>
        get() = _gamesCollection

    private val _navigateToGameDetails = MutableLiveData<MyGame?>()
    val navigateToGameDetails: LiveData<MyGame?>
        get() = _navigateToGameDetails

    init {
        getGamesCollection(false)
    }

    fun getGamesCollection(
        filtered: Boolean,
        gameType: GameType = GameType.GAME,
        name: String = ""
    ) {
        if (filtered) {
            if (gameType == GameType.GAME)
                _gamesCollection.addSource(repository.getOnlyGames()) {
                    reloadList(name, it)
                }
            else
                _gamesCollection.addSource(repository.getOnlyExpansions()) {
                    reloadList(name, it)
                }
        } else {
            _gamesCollection.addSource(repository.getFullCollection()) {
                reloadList(name, it)
            }
        }
    }

    private fun reloadList(name: String, list: List<MyGame>) {
        _fullGamesCollection = list
        if (name.isNotBlank()) filterGames(name)
        _gamesCollection.value = list
    }

    fun navigateToGameDetails(game: MyGame) {
        _navigateToGameDetails.value = game
    }

    fun doneNavigating() {
        _navigateToGameDetails.value = null
    }

    fun filterGames(name: String) {
        if (name.isNotBlank()) {
            _gamesCollection.value = _fullGamesCollection.filter { game ->
                game.name.contains(name, true)
            }
        }
    }
}