package com.akobusinska.letsplay.ui.gameSelection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.local.Filter
import com.akobusinska.letsplay.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectGameViewModel @Inject constructor(private val repository: GameRepository) :
    ViewModel() {

    private val _allGames = repository.getOnlyGames()
    val allGames: LiveData<List<MyGame>>
        get() = _allGames

    private val _selectedGamesCollection = MediatorLiveData<List<MyGame>>()
    val selectedGamesCollection: LiveData<List<MyGame>>
        get() = _selectedGamesCollection

    private val _unselectedGamesCollection = MutableLiveData<List<MyGame>>()
    val unselectedGamesCollection: LiveData<List<MyGame>>
        get() = _unselectedGamesCollection

    var currentFilter = Filter()

    init {
        _selectedGamesCollection.addSource(repository.getOnlyGames()) {
            _selectedGamesCollection.value = it
        }

        _unselectedGamesCollection.value = mutableListOf()
    }

    fun filterGamesCollection(filter: Filter) {
        currentFilter = filter

        _selectedGamesCollection.addSource(repository.getFilteredGames(filter)) { gamesList ->
            _selectedGamesCollection.value = gamesList

            for (filterMatchingGame in gamesList) {

                if (filterMatchingGame.gameType == GameType.EXPANSION) {

                    if (allGames.value != null) {
                        val parentGames =
                            allGames.value!!.filter { it.game_id == filterMatchingGame.parentGame }

                        if(parentGames.isNotEmpty())
                        _selectedGamesCollection.value =
                            _selectedGamesCollection.value?.toMutableList()?.apply {
                                if (!this.contains(parentGames[0]))
                                    add(parentGames[0])
                            }?.toList()
                    }

                    _selectedGamesCollection.value =
                        _selectedGamesCollection.value?.toMutableList()?.apply { remove(filterMatchingGame) }
                            ?.toList()
                }
            }

            for (unselectedGame in unselectedGamesCollection.value!!) {
                if (gamesList.none { it.game_id == unselectedGame.game_id }) {
                    _unselectedGamesCollection.value =
                        _unselectedGamesCollection.value?.toMutableList()?.apply { remove(unselectedGame) }
                            ?.toList()
                } else {
                    _selectedGamesCollection.value =
                        _selectedGamesCollection.value?.toMutableList()?.apply { remove(unselectedGame) }
                            ?.toList()
                }
            }
        }
    }

    fun removeItemFromList(game: MyGame) {
        _selectedGamesCollection.value =
            _selectedGamesCollection.value?.toMutableList()?.apply { remove(game) }?.toList()
        _unselectedGamesCollection.value =
            _unselectedGamesCollection.value?.toMutableList()?.apply { add(game) }?.toList()
    }

    fun revertItemIntoList(game: MyGame) {
        _selectedGamesCollection.value =
            _selectedGamesCollection.value?.toMutableList()?.apply { add(game) }?.toList()
        _unselectedGamesCollection.value =
            _unselectedGamesCollection.value?.toMutableList()?.apply { remove(game) }?.toList()
    }
}