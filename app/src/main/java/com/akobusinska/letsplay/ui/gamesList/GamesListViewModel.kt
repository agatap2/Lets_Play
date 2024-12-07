package com.akobusinska.letsplay.ui.gamesList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akobusinska.letsplay.data.entities.CollectionOwner
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.local.CollectionOwnerWithGames
import com.akobusinska.letsplay.data.repository.CollectionOwnerRepository
import com.akobusinska.letsplay.data.repository.GameRepository
import com.akobusinska.letsplay.data.xml.BoardGamesSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GamesListViewModel @Inject constructor(
    private val repository: GameRepository,
    private val userRepository: CollectionOwnerRepository
) : ViewModel() {

    private val _gamesCollection = MutableLiveData<List<MyGame>>()
    val gamesCollection: LiveData<List<MyGame>>
        get() = _gamesCollection

    private val _statusOfNewUser = MutableLiveData<GameRepository.RequestStatus>()
    val statusOfNewUser: LiveData<GameRepository.RequestStatus>
        get() = _statusOfNewUser

    private val _statusOfRefresh = MutableLiveData<GameRepository.RequestStatus>()
    val statusOfRefresh: LiveData<GameRepository.RequestStatus>
        get() = _statusOfRefresh

    private var _users = MediatorLiveData<List<CollectionOwner>>()
    val users: LiveData<List<CollectionOwner>>
        get() = _users

    private var newestCollection = mutableListOf<String>()
    private val _navigateToGameDetails = MutableLiveData<MyGame?>()
    val navigateToGameDetails: LiveData<MyGame?>
        get() = _navigateToGameDetails

    init {
        _users.addSource(userRepository.getAllUsers()) {
            _users.value = it
        }
    }

    fun checkIfUserExists(user: String): Boolean {
        return users.value?.any { it.name == user } ?: false
    }

    fun updateGamesCollection(
        gameType: GameType?,
        name: String = "",
        ownerId: Long,
        ownersWithGames: List<CollectionOwnerWithGames>
    ) {

        viewModelScope.launch {

            val userList =
                ownersWithGames.filter { ownersWithGames -> ownersWithGames.collectionOwner.collectionOwnerId == ownerId }

            _gamesCollection.value = if (userList.isNotEmpty()) {
                var gamesList = userList[0].games
                if (gameType != null) {
                    gamesList = gamesList.filter { it.gameType == gameType }
                }
                if (name.isNotBlank()) {
                    gamesList = gamesList.filter { it.name.contains(name, true) }
                }
                gamesList
            } else {
                emptyList()
            }
        }
    }

    fun getUserByName(name: String): LiveData<CollectionOwner> {
        return userRepository.getUserByName(name)
    }

    fun getUserCollection(name: String) {

        viewModelScope.launch {
            try {
                _statusOfNewUser.value = GameRepository.RequestStatus.LOADING
                newestCollection.clear()
                userRepository.downloadCollectionOwner(name)?.games?.let {
                    newestCollection.addAll(
                        it
                    )
                }
            } catch (e: Exception) {
                _statusOfNewUser.value = GameRepository.RequestStatus.ERROR
            }
        }
    }

    fun reDownloadUserCollection(name: String) {
        viewModelScope.launch {
            try {
                _statusOfNewUser.value = GameRepository.RequestStatus.LOADING
                _statusOfRefresh.value = GameRepository.RequestStatus.LOADING
                newestCollection.clear()
                userRepository.refreshUsersCollection(name).let {
                    newestCollection.addAll(
                        it
                    )
                }
                _statusOfRefresh.value = GameRepository.RequestStatus.DONE
            } catch (e: Exception) {
                _statusOfNewUser.value = GameRepository.RequestStatus.ERROR
            }
        }
    }

    fun stopLoadIcon(reload: Boolean = false) {
        if (_statusOfNewUser.value == GameRepository.RequestStatus.LOADING) {
            _statusOfNewUser.value = GameRepository.RequestStatus.DONE
        }
        if (reload) {
            _statusOfNewUser.value = GameRepository.RequestStatus.LOADING
        }
    }

    fun getLastCollectionOwner() = userRepository.getLastUser()

    fun getCollectionOwnersWithGames() = repository.getFullCrossRefCollection()

    fun updateGamesList(user: CollectionOwner, update: Boolean = false) {

        viewModelScope.launch {
            newestCollection.map {
                BoardGamesSearchResult(
                    it
                )
            }.let {
                repository.downloadGamesWithDetailsList(
                    user.collectionOwnerId, it, true, update
                )
            }
        }
    }

    fun updateUserCustomName(user: CollectionOwner?, customName: String) {

        viewModelScope.launch {
            val updatedUser = user.let {
                it?.collectionOwnerId?.let { id ->
                    CollectionOwner(
                        id, it.name, customName, it.games
                    )
                }
            }
            if (updatedUser != null) {
                userRepository.updateUser(updatedUser)
            }
        }
    }

    fun navigateToGameDetails(game: MyGame) {
        _navigateToGameDetails.value = game
    }

    fun doneNavigating() {
        _navigateToGameDetails.value = null
    }
}