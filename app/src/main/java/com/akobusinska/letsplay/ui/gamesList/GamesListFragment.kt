package com.akobusinska.letsplay.ui.gamesList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.databinding.FragmentGamesListBinding
import com.akobusinska.letsplay.ui.gamesList.BasicGamesListAdapter.GamesListListener
import com.akobusinska.letsplay.utils.afterTextChanged
import com.akobusinska.letsplay.utils.bindRecyclerView
import com.akobusinska.letsplay.utils.showInput
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
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

        val adapter = BasicGamesListAdapter(GamesListListener { game ->
            viewModel.navigateToGameDetails(game.id)
        })

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.fullGamesList.apply {
            this.adapter = adapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }

        binding.all.setOnClickListener {
            if (!allSelected)
                viewModel.getGamesCollection(false, name = getFilter())

            allDisplayed()
        }

        binding.gameFilterButton.setOnClickListener {
            if (!gamesSelected)
                viewModel.getGamesCollection(true, GameType.GAME, getFilter())

            onlyGamesDisplayed()
        }

        binding.expansionFilterButton.setOnClickListener {
            if (!expansionsSelected)
                viewModel.getGamesCollection(true, GameType.EXPANSION, getFilter())

            onlyExpansionsDisplayed()
        }

        binding.addGameButton.setOnClickListener {

            val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.dialog_text_input, null)

            MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setTitle(R.string.provide_game_name)
                .setPositiveButton(R.string.ok) { _, _ ->
                    this.findNavController().navigate(
                        GamesListFragmentDirections.searchForGame(
                            dialogView.findViewById<TextInputLayout>
                                (R.id.dialog_text_input_layout)?.editText?.text.toString()
                        )
                    )
                }
                .setNegativeButton(R.string.cancel, null)
                .showInput(
                    R.id.dialog_text_input_layout,
                    R.string.game_name
                )
        }

        binding.searchBar.afterTextChanged {
            viewModel.filterGames(it)
        }

        viewModel.navigateToGameDetails.observe(viewLifecycleOwner) { gameId ->
            gameId?.let {
                // TODO Navigate to details fragment
                viewModel.doneNavigating()
            }
        }

        viewModel.gamesCollection.observe(viewLifecycleOwner) { gamesList ->
            binding.fullGamesList.bindRecyclerView(gamesList)
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