package com.akobusinska.letsplay.ui.gameDetails

import androidx.lifecycle.*
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailsViewModel @Inject constructor(
    private val repository: GameRepository,
    state: SavedStateHandle
) : ViewModel() {

    private val _navigateToGameEditionForm = MutableLiveData<MyGame?>()
    val navigateToGameEditionForm: LiveData<MyGame?>
        get() = _navigateToGameEditionForm

    private val _game = MutableLiveData<MyGame>()
    val game: LiveData<MyGame>
        get() = _game

    private var _parentAndChildren = MediatorLiveData<String>()
    val parentAndChildren: LiveData<String>
        get() = _parentAndChildren

    init {
        _game.value = state.get<MyGame>("game")
    }

    fun deleteGameFromDatabase(game: MyGame) {
        viewModelScope.launch {
            repository.deleteGame(game)
        }
    }

    fun getParentGame(id: Int) {
        _parentAndChildren.addSource(repository.getGameById(id)) {
            if (it != null)
                _parentAndChildren.value = it.name
            else
                _parentAndChildren.value = ""
        }
    }

    fun getOwnedExpansions(parentId: Int) {
        _parentAndChildren.addSource(repository.getExpansionsListById(parentId)) { expansionsList ->
            _parentAndChildren.value = ""
            if (expansionsList != null)
                for (game in expansionsList) {
                    if (game.game_id != parentId)
                        _parentAndChildren.value = _parentAndChildren.value + "<br>" + game.name
                }
        }
    }

    fun navigateToGameEditionForm(game: MyGame?) {
        _navigateToGameEditionForm.value = game
    }

    fun doneNavigating() {
        _navigateToGameEditionForm.value = null
    }

}