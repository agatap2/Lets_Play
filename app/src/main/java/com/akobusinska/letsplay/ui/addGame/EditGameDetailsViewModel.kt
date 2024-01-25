package com.akobusinska.letsplay.ui.addGame

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.*
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.CollectionOwnerWithGames
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.repository.CollectionOwnerRepository
import com.akobusinska.letsplay.data.repository.GameRepository
import com.akobusinska.letsplay.utils.RefreshableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditGameDetailsViewModel @SuppressLint("StaticFieldLeak")
@Inject constructor(
    private val repository: GameRepository,
    private val userRepository: CollectionOwnerRepository,
    state: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _newGame = MutableLiveData<MyGame>()
    val newGame: LiveData<MyGame>
        get() = _newGame

    private val _parentGame = MediatorLiveData<MyGame>()
    val parentGame: LiveData<MyGame>
        get() = _parentGame

    private val _foundGamesList = MediatorLiveData<List<MyGame>>()
    val foundGamesList: LiveData<List<MyGame>>
        get() = _foundGamesList

    private val _allGamesList = RefreshableLiveData { repository.getFullCollection() }
    val allGamesList: LiveData<List<MyGame>>
        get() = _allGamesList

    init {
        _newGame.value = state.get<MyGame>("game")

        _allGamesList.addSource(repository.getFullCollection()) {
            _allGamesList.value = it
        }
    }

    fun refresh() {
        _allGamesList.refresh()
    }

    var parentGameName = newGame.map {
        if (it.gameType == GameType.EXPANSION && it.gameId != it.parentGame)
            getParentGame(it.parentGame)
        context.getString(R.string.parent_game, "?")
    }

    val minNumberOfPlayers = newGame.map { game ->
        game.minPlayers.toString()
    }

    val maxNumberOfPlayers = newGame.map { game ->
        if (game.maxPlayers > 20)
            "20+"
        else
            game.maxPlayers.toString()
    }

    val minPlaytime = newGame.map { game ->
        game.minPlaytime.toString()
    }

    val maxPlaytime = newGame.map { game ->
        if (game.maxPlaytime > 120)
            "2h+"
        else
            game.maxPlaytime.toString()
    }

    val minAge = newGame.map { game ->
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
        else {
            println("LIST: " + newGame.value?.minPlaytime!!.toString())
            listOf(newGame.value?.minPlaytime!!.toFloat(), newGame.value?.maxPlaytime!!.toFloat())
        }
    }

    fun getParentGame(id: Int) {
        _parentGame.addSource(repository.getGameById(id)) {
            if (it != null)
                _parentGame.value = it
        }
    }

    fun insertGameIntoDatabase() {
        viewModelScope.launch {
            newGame.value?.let {
                repository.insertGame(it)
            }
        }
    }

    fun insertGameWithOwnerIntoDatabase() {
        viewModelScope.launch {
            newGame.value?.let {
                userRepository.insertUserWithGames(CollectionOwnerWithGames(1, it.gameId))
            }
        }
    }

    fun updateGameInDatabase() {
        viewModelScope.launch {
            newGame.value?.let { repository.updateGame(it) }
        }
    }

    fun getSearchResult() {
        _foundGamesList.addSource(repository.getOnlyGames()) {
            _foundGamesList.value = it
        }
    }

    fun updateGameFields(
        id: Int,
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
                if (id != -1) game.gameId = id
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
        viewModelScope.launch {
            repository.updateGame(parent)
        }
    }

}