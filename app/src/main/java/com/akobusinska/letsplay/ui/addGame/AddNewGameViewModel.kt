package com.akobusinska.letsplay.ui.addGame

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.*
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.GameType
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

    private val _foundGamesList = MediatorLiveData<List<MyGame>>()
    val foundGamesList: LiveData<List<MyGame>>
        get() = _foundGamesList

    init {
        _newGame.value = state.get<MyGame>("game")
    }

    var parentGameName = Transformations.map(newGame) {
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

    fun insertGameIntoDatabase() {
        viewModelScope.launch {
            newGame.value?.let { repository.insertGame(it) }
        }
    }

    fun getSearchResult() {
        _foundGamesList.addSource(repository.getOnlyGames()) {
            _foundGamesList.value = it
        }
    }

    fun updateGameFields(
        name: String,
        minPlayers: Int,
        maxPlayers: Int,
        recommendedForMore: Boolean,
        minPlaytime: Int,
        maxPlaytime: Int,
        minAge: Int,
        thumbUrl: String,
        gameType: GameType,
        parent: Int
    ) {
        _newGame.value.let { game ->
            if (game != null) {
                game.name = name
                game.minPlayers = minPlayers
                game.maxPlayers = maxPlayers
                game.recommendedForMorePlayers = recommendedForMore
                game.minPlaytime = minPlaytime
                game.maxPlaytime = maxPlaytime
                game.minAge = minAge
                game.thumbURL = thumbUrl
                game.gameType = gameType
                game.parentGame = parent
            }
        }
    }

    fun updateParentGame(parent: MyGame) {
        val parentGame = repository.getGameById(parent.id).value
        parentGame?.expansions?.add(newGame.value!!.id)

        viewModelScope.launch {
            if (parentGame != null) {
                repository.updateGame(parentGame)
            }
        }
    }

}