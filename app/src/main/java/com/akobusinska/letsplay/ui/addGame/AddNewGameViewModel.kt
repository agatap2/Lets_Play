package com.akobusinska.letsplay.ui.addGame

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.*
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewGameViewModel @SuppressLint("StaticFieldLeak")
@Inject constructor(
    private val repository: GameRepository,
    state: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _newGame = MutableLiveData<MyGame>()
    val newGame: LiveData<MyGame>
        get() = _newGame

    private val _status = MutableLiveData<GameRepository.RequestStatus>()
    val status: LiveData<GameRepository.RequestStatus>
        get() = _status

    private val _foundGamesList = MutableLiveData<List<MyGame>>()
    val foundGamesList: LiveData<List<MyGame>>
        get() = _foundGamesList

    init {
        _newGame.value = state.get<MyGame>("game")
    }

    fun getSearchResult() {
        viewModelScope.launch {
            try {
                _status.value = GameRepository.RequestStatus.LOADING
                _foundGamesList.value = repository.getOnlyGames().value
                _status.value = GameRepository.RequestStatus.DONE
            } catch (e: Exception) {
                _status.value = GameRepository.RequestStatus.ERROR
                println("ERROR: ${e.message}")
            }
        }
    }

    val parentGame = Transformations.map(newGame) { game ->
        if (game.parentGame != game.id)
            context.getString(R.string.parent_game,
                game.parentGame.let { repository.getGameById(it).value?.name ?: "" })
        else
            context.getString(R.string.parent_game, "?")
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

    fun updateParentGame(id: Int) {
        val parentGame = repository.getGameById(id).value
        parentGame?.expansions?.add(newGame.value!!.id)

        viewModelScope.launch {
            if (parentGame != null) {
                repository.updateGame(parentGame)
            }
        }
    }

}