package com.akobusinska.letsplay.ui.gamesList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.databinding.FragmentGamesListBinding
import com.akobusinska.letsplay.ui.gamesList.GamesListAdapter.GamesListListener
import com.akobusinska.letsplay.utils.afterTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GamesListFragment : Fragment() {

    private val viewModel: GamesListViewModel by viewModels()
    private lateinit var binding: FragmentGamesListBinding
    private var allSelected = true
    private var gamesSelected = false
    private var expansionsSelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_games_list, container, false
        )

        val adapter = GamesListAdapter(GamesListListener { gameId ->
            viewModel.navigateToGameDetails(gameId)
        })

        binding.lifecycleOwner = this
        binding.fullGamesList.adapter = adapter
        binding.viewModel = viewModel

        binding.all.setOnClickListener {
            if (!allSelected) {
                viewModel.getGamesCollection(false, name = getFilter())
                allDisplayed()
            }
        }

        binding.gameFilterButton.setOnClickListener {
            if (!gamesSelected) {
                viewModel.getGamesCollection(true, GameType.GAME, getFilter())
                onlyGamesDisplayed()
            }
        }

        binding.expansionFilterButton.setOnClickListener {
            if (!expansionsSelected) {
                viewModel.getGamesCollection(true, GameType.EXPANSION, getFilter())
                onlyExpansionsDisplayed()
            }
        }

        binding.addGameButton.setOnClickListener {
            // Navigate to add new game fragment
        }

        binding.searchBar.afterTextChanged {
            viewModel.filterGames(it)
        }

        viewModel.navigateToGameDetails.observe(viewLifecycleOwner) { gameId ->
            gameId?.let {
                // Navigate to details fragment
                viewModel.doneNavigating()
            }
        }

        return binding.root
    }

    private fun getFilter() = binding.searchBar.editableText.toString()

    private fun allDisplayed() {
        allSelected = true
        gamesSelected = false
        expansionsSelected = false
    }

    private fun onlyGamesDisplayed() {
        allSelected = false
        gamesSelected = true
        expansionsSelected = false
    }

    private fun onlyExpansionsDisplayed() {
        allSelected = false
        gamesSelected = false
        expansionsSelected = true
    }
}