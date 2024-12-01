package com.akobusinska.letsplay.ui.gameSelection

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.CollectionOwner
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
    var randomGame = MyGame()
    private var collectionOwner: CollectionOwner? = null

    init {
        _unselectedGamesCollection.value = mutableListOf()
    }

    fun setCollectionOwner(collectionOwner: CollectionOwner?) {
        this.collectionOwner = collectionOwner

        _selectedGamesCollection.addSource(repository.getFullCrossRefCollection()) {
            _selectedGamesCollection.value =
                it.filter { ownerWithGames -> ownerWithGames.collectionOwner.collectionOwnerId == collectionOwner?.collectionOwnerId }[0].games
        }
    }

    fun selectRandomGame() {
        randomGame = selectedGamesCollection.value?.random()!!
    }

    fun filterGamesCollection(filter: Filter) {

        _selectedGamesCollection.addSource(repository.getFullCrossRefCollection()) { list ->
            val gamesList =
                list.filter { ownerWithGames -> ownerWithGames.collectionOwner.collectionOwnerId == collectionOwner?.collectionOwnerId }[0].games.filter { game ->
                    game.maxPlayers >= filter.numberOfPlayers &&
                            game.minPlayers <= filter.numberOfPlayers &&
                            game.minAge <= filter.age &&
                            (!filter.excludeRecommendedForMore || !game.recommendedForMorePlayers) &&
                            game.minPlaytime <= filter.maxPlaytime
                }

            _selectedGamesCollection.value = gamesList

            for (filterMatchingGame in gamesList) {

                if (filterMatchingGame.gameType == GameType.EXPANSION) {

                    if (allGames.value != null) {
                        val parentGames =
                            allGames.value!!.filter { it.gameId == filterMatchingGame.parentGame }

                        if (parentGames.isNotEmpty())
                            _selectedGamesCollection.value =
                                _selectedGamesCollection.value?.toMutableList()?.apply {
                                    if (!this.contains(parentGames[0]))
                                        add(parentGames[0])
                                }?.toList()
                    }

                    _selectedGamesCollection.value =
                        _selectedGamesCollection.value?.toMutableList()
                            ?.apply { remove(filterMatchingGame) }
                            ?.toList()
                }
            }

            for (unselectedGame in unselectedGamesCollection.value!!) {
                if (gamesList.none { it.gameId == unselectedGame.gameId }) {
                    _unselectedGamesCollection.value =
                        _unselectedGamesCollection.value?.toMutableList()
                            ?.apply { remove(unselectedGame) }
                            ?.toList()
                } else {
                    _selectedGamesCollection.value =
                        _selectedGamesCollection.value?.toMutableList()
                            ?.apply { remove(unselectedGame) }
                            ?.toList()
                }
            }
        }
    }

    fun removeItemFromList(game: MyGame): Boolean {
        _selectedGamesCollection.value =
            _selectedGamesCollection.value?.toMutableList()?.apply { remove(game) }?.toList()
        _unselectedGamesCollection.value =
            _unselectedGamesCollection.value?.toMutableList()?.apply { add(game) }?.toList()
        return _unselectedGamesCollection.value.isNullOrEmpty()
    }

    fun revertItemIntoList(game: MyGame): Boolean {
        _selectedGamesCollection.value =
            _selectedGamesCollection.value?.toMutableList()?.apply { add(game) }?.toList()
        _unselectedGamesCollection.value =
            _unselectedGamesCollection.value?.toMutableList()?.apply { remove(game) }?.toList()
        return _unselectedGamesCollection.value.isNullOrEmpty()
    }

    fun getSelectedGamesNameList(context: Context): String {
        var namesList = ""
        val gamesList = ArrayList<String>()

        if (selectedGamesCollection.value != null) {
            for (game: MyGame in selectedGamesCollection.value!!)
                gamesList.add(game.name)
            gamesList.sort()
        }

        if (gamesList.isNotEmpty()) {
            for (gameName: String in gamesList)
                namesList += "\n$gameName, "
            namesList = context.getString(R.string.games_we_can_play) + namesList.substring(
                0,
                namesList.length - 2
            )
        }

        return namesList
    }
}