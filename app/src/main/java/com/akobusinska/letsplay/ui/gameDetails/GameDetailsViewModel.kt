package com.akobusinska.letsplay.ui.gameDetails

import android.content.Context
import androidx.core.text.HtmlCompat
import androidx.lifecycle.*
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GameDetailsViewModel @Inject constructor(
    private val repository: GameRepository,
    state: SavedStateHandle,
    @ApplicationContext private val context: Context
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

    val displayGameType = Transformations.map(game) { game ->
        if (game.gameType == GameType.GAME) context.applicationContext.getString(R.string.game)
            .uppercase(Locale.ROOT)
        else context.applicationContext.getString(R.string.expansion).uppercase(Locale.ROOT)
    }

    val displayNumberOfPlayers = Transformations.map(game) { game ->
        if (game.maxPlayers < 20)
            HtmlCompat.fromHtml(
                context.applicationContext.getString(
                    R.string.number_of_players_value,
                    game.minPlayers,
                    game.maxPlayers
                ), HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        else
            HtmlCompat.fromHtml(
                context.applicationContext.getString(
                    R.string.large_number_of_players_value,
                    game.minPlayers,
                    game.maxPlayers
                ), HtmlCompat.FROM_HTML_MODE_LEGACY
            )
    }

    val displayPlaytime = Transformations.map(game) { game ->
        if (game.maxPlaytime < 120)
            HtmlCompat.fromHtml(
                context.applicationContext.getString(
                    R.string.playtime_value,
                    game.minPlaytime,
                    game.maxPlaytime
                ), HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        else
            HtmlCompat.fromHtml(
                context.applicationContext.getString(
                    R.string.long_playtime,
                    game.minPlaytime,
                    game.maxPlaytime
                ), HtmlCompat.FROM_HTML_MODE_LEGACY
            )
    }

    val displayMinAge = Transformations.map(game) { game ->
        HtmlCompat.fromHtml(
            context.applicationContext.getString(
                R.string.min_age_value,
                game?.minAge ?: 3
            ), HtmlCompat.FROM_HTML_MODE_LEGACY
        )
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
                    if (game.id != parentId)
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