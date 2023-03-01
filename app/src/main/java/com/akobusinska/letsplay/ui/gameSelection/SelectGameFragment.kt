package com.akobusinska.letsplay.ui.gameSelection

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.databinding.FragmentSelectGameBinding
import com.akobusinska.letsplay.utils.bindDetailedRecyclerView
import com.akobusinska.letsplay.utils.bindSimpleRecyclerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectGameFragment : Fragment() {

    val viewModel: SelectGameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentSelectGameBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_game, container, false)

        val selectedGamesAdapter =
            DetailedGamesListAdapter(DetailedGamesListAdapter.GamesListListener { game ->
                changeGuidelinePosition(viewModel.removeItemFromList(game), binding)
            })

        val removedGamesAdapter =
            SimpleGamesListAdapter(SimpleGamesListAdapter.GamesListListener { game ->
                changeGuidelinePosition(viewModel.revertItemIntoList(game), binding)
            })

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.filter_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.share -> {
                        val gamesList = viewModel.getSelectedGamesNameList(requireContext())
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, gamesList)
                            type = "text/plain"
                        }

                        val shareIntent = Intent.createChooser(sendIntent, null)
                        if (gamesList.isNotBlank())
                            startActivity(shareIntent)
                    }
                    R.id.select_random -> {
                        viewModel.selectRandomGame()
                        if (!viewModel.selectedGamesCollection.value.isNullOrEmpty())
                            DialogRandomGameFragment().show(
                                requireActivity().supportFragmentManager,
                                "random"
                            )
                    }
                    R.id.filter -> {
                        viewModel.allGames.observe(viewLifecycleOwner) {
                            if (!viewModel.allGames.value.isNullOrEmpty())
                                DialogGamesFilteringFragment().show(
                                    requireActivity().supportFragmentManager,
                                    "filter"
                                )
                        }
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)

        binding.lifecycleOwner = this
        binding.selectedGamesList.adapter = selectedGamesAdapter
        binding.removedGamesList.adapter = removedGamesAdapter

        viewModel.selectedGamesCollection.observe(viewLifecycleOwner) {
            binding.selectedGamesList.bindDetailedRecyclerView(it)
        }

        viewModel.unselectedGamesCollection.observe(viewLifecycleOwner) {
            binding.removedGamesList.bindSimpleRecyclerView(it)
        }

        return binding.root
    }

    private fun changeGuidelinePosition(emptyList: Boolean, binding: FragmentSelectGameBinding) {
        if(emptyList)
            binding.guideline.setGuidelinePercent(1.0F)
        else
            binding.guideline.setGuidelinePercent(0.65F)
    }
}