package com.akobusinska.letsplay.ui.gameDetails

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.text.HtmlCompat
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.databinding.FragmentGameDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameDetailsFragment : Fragment() {

    val viewModel: GameDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentGameDetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_game_details, container, false)
        var game: MyGame? = null
        val parentAndChildren: TextView = binding.parentAndChildren
        val recommendedForMorePlayers = binding.recommendedForMore
        val recommendedForMorePlayersIcon = binding.recommendedForMoreIcon

        binding.lifecycleOwner = this
        binding.game = null

        viewModel.game.observe(viewLifecycleOwner) {
            game = it
            binding.game = it

            if (game?.gameType == GameType.GAME)
                viewModel.getOwnedExpansions(it.game_id)
            else
                viewModel.getParentGame(it.parentGame)

            if (game?.recommendedForMorePlayers == true) {
                recommendedForMorePlayers.visibility = View.VISIBLE
                recommendedForMorePlayersIcon.visibility = View.VISIBLE
            } else {
                recommendedForMorePlayers.visibility = View.GONE
                recommendedForMorePlayersIcon.visibility = View.GONE
            }
        }

        viewModel.parentAndChildren.observe(viewLifecycleOwner) {
            if (it != null)
                if (game?.gameType == GameType.EXPANSION) {
                    parentAndChildren.text =
                        HtmlCompat.fromHtml(
                            requireContext().applicationContext.getString(
                                R.string.parent_game_value,
                                it.ifBlank { "?" }
                            ), HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                } else {
                    parentAndChildren.text =
                        HtmlCompat.fromHtml(
                            requireContext().applicationContext.getString(
                                R.string.owned_expansions_value,
                                it.ifBlank { "-" }
                            ), HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                }
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.details_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.edit -> {
                        viewModel.navigateToGameEditionForm(game)
                    }
                    R.id.revert -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(resources.getString(R.string.confirm_delete_title))
                            .setMessage(resources.getString(R.string.confirm_delete_message))
                            .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                                dialog.cancel()
                            }
                            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                                game?.let { viewModel.deleteGameFromDatabase(it) }
                                navigateToGamesList()
                            }
                            .show()
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)

        viewModel.navigateToGameEditionForm.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    GameDetailsFragmentDirections.navigateToGamesEditionForm(
                        it,
                        false
                    )
                )
                viewModel.doneNavigating()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateToGamesList()
                }
            }
        )

        return binding.root
    }

    fun navigateToGamesList() {
        findNavController().navigate(GameDetailsFragmentDirections.navigateToCollectionList())
    }
}