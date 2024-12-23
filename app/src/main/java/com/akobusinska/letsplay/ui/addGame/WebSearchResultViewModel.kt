package com.akobusinska.letsplay.ui.addGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.repository.GameRepository
import com.akobusinska.letsplay.data.repository.GameRepository.RequestStatus
import com.akobusinska.letsplay.data.xml.BoardGamesSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebSearchResultViewModel @Inject constructor(private val repository: GameRepository) :
    ViewModel() {

    private val _status = MutableLiveData<RequestStatus>()
    val status: LiveData<RequestStatus>
        get() = _status

    private val _foundGamesList = MutableLiveData<List<MyGame>>()
    val foundGamesList: LiveData<List<MyGame>>
        get() = _foundGamesList

    private val _navigateToNewGameForm = MutableLiveData<MyGame?>()
    val navigateToNewGameForm: LiveData<MyGame?>
        get() = _navigateToNewGameForm

    fun navigateToNewGameForm(game: MyGame?) {
        _navigateToNewGameForm.value = game
    }

    fun doneNavigating() {
        _navigateToNewGameForm.value = null
    }

    fun getSearchResult(name: String) {
        var searchResultList: List<BoardGamesSearchResult>

        viewModelScope.launch {
            try {
                _status.value = RequestStatus.LOADING
                searchResultList =
                    repository.downloadGamesList(name).take(20) as List<BoardGamesSearchResult>
                _foundGamesList.value = repository.downloadGamesWithDetailsList(1, searchResultList, false)

                _status.value = RequestStatus.DONE
            } catch (e: Exception) {
                _status.value = RequestStatus.ERROR
            }
        }
    }
}