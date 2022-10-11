package com.akobusinska.letsplay.ui.gameSelection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.local.Filter
import com.akobusinska.letsplay.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectGameViewModel @Inject constructor(private val repository: GameRepository) :
    ViewModel() {

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
        _selectedGamesCollection.addSource(repository.getFilteredGames(filter)) {
            _selectedGamesCollection.value = it
            for (game in unselectedGamesCollection.value!!) {
                if (!it.contains(game)) {
                    _selectedGamesCollection.value =
                        _selectedGamesCollection.value?.toMutableList()?.apply { remove(game) }
                            ?.toList()
                    _unselectedGamesCollection.value =
                        _unselectedGamesCollection.value?.toMutableList()?.apply { remove(game) }
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