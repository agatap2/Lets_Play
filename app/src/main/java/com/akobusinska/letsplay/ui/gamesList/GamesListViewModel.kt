package com.akobusinska.letsplay.ui.gamesList

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akobusinska.letsplay.data.entities.CollectionOwner
import com.akobusinska.letsplay.data.entities.CollectionOwnerWithGames
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.repository.CollectionOwnerRepository
import com.akobusinska.letsplay.data.repository.GameRepository
import com.akobusinska.letsplay.data.xml.BoardGamesSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GamesListViewModel @Inject constructor(
    private val repository: GameRepository,
    private val userRepository: CollectionOwnerRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private lateinit var _fullGamesCollection: List<MyGame>
    private val _gamesCollection = MediatorLiveData<List<MyGame>>()
    val gamesCollection: LiveData<List<MyGame>>
        get() = _gamesCollection

    private val _status = MutableLiveData<GameRepository.RequestStatus>()
    val status: LiveData<GameRepository.RequestStatus>
        get() = _status

    private var _users = MediatorLiveData<List<CollectionOwner>>()
    val users: LiveData<List<CollectionOwner>>
        get() = _users

    private var _userCollection = MediatorLiveData<List<MyGame>>()
    val userCollection: LiveData<List<MyGame>>
        get() = _userCollection

    private val _navigateToGameDetails = MutableLiveData<MyGame?>()
    val navigateToGameDetails: LiveData<MyGame?>
        get() = _navigateToGameDetails

    init {
        getGamesCollection(false)
        getAllUsersNames()
    }

    fun getAllUsersNames() {
        _users.addSource(userRepository.getAllUsers()) {
            _users.value = it.map { u -> u.collectionOwner }
        }
    }

    fun getGamesCollection(
        filtered: Boolean,
        gameType: GameType = GameType.GAME,
        name: String = "",
        ownerName: String = "Default"
    ) {
        if (filtered) {
            if (gameType == GameType.GAME)
                _gamesCollection.addSource(userRepository.getGamesUserCollection(ownerName)) {
                    reloadList(name, it)
                }
            else
                _gamesCollection.addSource(userRepository.getExpansionsUserCollection(ownerName)) {
                    reloadList(name, it)
                }
        } else {
            _gamesCollection.addSource(userRepository.getFullUserCollection(ownerName)) {
                reloadList(name, it)
            }
        }
    }

    fun getUserCollection(name: String) {
        var userCollection: CollectionOwner
        var tempGameId: Int
        var userId: Int

        viewModelScope.launch {
            try {
                _status.value = GameRepository.RequestStatus.LOADING

                userCollection = userRepository.downloadCollectionOwner(name)

                val temp = _users.value?.filter { it.name == name }

                userId = if (temp?.isEmpty() != false)
                    userRepository.insertUser(userCollection).toInt()
                else temp[0].collectionOwnerId

                userCollection.games.forEach {
                    userRepository.insertUserWithGames(CollectionOwnerWithGames(userId, it.toInt()))
                }

                _userCollection.value =
                    repository.downloadGamesWithDetailsList(userCollection.games.map {
                        BoardGamesSearchResult(
                            it
                        )
                    })

                _userCollection.value?.forEach { game ->
                    if (game.gameType == GameType.GAME && game.expansions.isNotEmpty()) {
                        val iterator = game.expansions.iterator()
                        while (iterator.hasNext()) {
                            val expansionId = iterator.next()
                            val expansion =
                                _userCollection.value!!.find { it.gameId == expansionId }
                            if (expansion == null || expansion.gameType == GameType.GAME) {
                                iterator.remove()
                            } else {
                                expansion.parentGame = game.parentGame
                            }
                        }
                    }
                }

                _userCollection.value?.forEach { game ->
                    if (game.gameType == GameType.EXPANSION) game.expansions.clear()
                    game.let {
                        tempGameId = repository.insertGame(it).toInt()
                        userRepository.insertUserWithGames(
                            CollectionOwnerWithGames(
                                userId,
                                tempGameId
                            )
                        )
                    }
                }

                _status.value = GameRepository.RequestStatus.DONE

            } catch (e: Exception) {
                _status.value = GameRepository.RequestStatus.ERROR
                println("ERROR: ${e.message}")
            }
        }
    }

    private fun reloadList(name: String, list: List<MyGame>) {

//        val backup = Backup(context)
//        backup.writeJSON(list)

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
        } else getGamesCollection(false)
    }
}