package com.akobusinska.letsplay.ui.gamesList

import androidx.lifecycle.*
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GamesListViewModel @Inject constructor(private val repository: GameRepository) : ViewModel() {

    private val _gamesCollection = MediatorLiveData<List<MyGame>>()
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

        if (filtered) {
            if (gameType == GameType.GAME)
                _gamesCollection.addSource(repository.getOnlyGames()) {
                    _gamesCollection.value = it
                }
            else
                _gamesCollection.addSource(repository.getOnlyExpansions()) {
                    _gamesCollection.value = it
                }
        } else {
            _gamesCollection.addSource(repository.getFullCollection()) {
                _gamesCollection.value = it
            }
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
        if (name.isNotBlank())
            Transformations.map(_gamesCollection) { myGames ->
                myGames.filter { game ->
                    game.name.contains(name)
                }
            }
    }
}