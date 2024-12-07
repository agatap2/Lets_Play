package com.akobusinska.letsplay.ui.gameDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akobusinska.letsplay.data.entities.GameType
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

    fun deleteGameFromUserCollection(game: MyGame) {
        viewModelScope.launch {
            repository.deleteGameFromDefaultCollection(game.gameId)
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

    fun getOwnedExpansions(parentId: Long, collectionOwnerId: Long) {
        _parentAndChildren.addSource(repository.getFullCrossRefCollection()) { list ->
            _parentAndChildren.value = ""
            try {
                val expansionsList =
                    list.filter { ownerWithGames -> ownerWithGames.collectionOwner.collectionOwnerId == collectionOwnerId }[0].games
                        .filter { game -> game.gameType == GameType.EXPANSION && game.parentGame == parentId}
                for (game in expansionsList) {
                    if (game.gameId != parentId)
                        _parentAndChildren.value = _parentAndChildren.value + "<br>" + game.name
                }
            } catch (e: Exception) {

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