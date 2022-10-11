package com.akobusinska.letsplay.ui.gameSelection

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.databinding.FragmentSelectGameBinding
import com.akobusinska.letsplay.utils.bindDetailedRecyclerView
import com.akobusinska.letsplay.utils.bindSimpleRecyclerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectGameFragment : Fragment() {

    val viewModel: SelectGameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentSelectGameBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_game, container, false)

        val selectedGamesAdapter =
            DetailedGamesListAdapter(DetailedGamesListAdapter.GamesListListener { game ->
                viewModel.removeItemFromList(game)
            })

        val removedGamesAdapter =
            SimpleGamesListAdapter(SimpleGamesListAdapter.GamesListListener { game ->
                viewModel.revertItemIntoList(game)
            })

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.filter_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.filter -> {
                        DialogGamesFilteringFragment(viewModel).show(
                            requireActivity().supportFragmentManager,
                            "filter"
                        )
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

}