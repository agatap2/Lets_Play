package com.akobusinska.letsplay.ui.addGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.akobusinska.letsplay.data.entities.MyGame

class AddNewGameViewModelFactory(private val game: MyGame?) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddNewGameViewModel::class.java)) {
            return AddNewGameViewModel(game) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}