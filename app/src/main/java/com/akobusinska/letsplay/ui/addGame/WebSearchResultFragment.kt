package com.akobusinska.letsplay.ui.addGame

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
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.databinding.FragmentWebSearchResultBinding
import com.akobusinska.letsplay.ui.gamesList.BasicGamesListAdapter
import com.akobusinska.letsplay.ui.gamesList.BasicGamesListAdapter.GamesListListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebSearchResultFragment : Fragment() {

    private val viewModel: WebSearchResultViewModel by viewModels()
    private lateinit var binding: FragmentWebSearchResultBinding
    private lateinit var gameName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_web_search_result, container, false)

        gameName = WebSearchResultFragmentArgs.fromBundle(arguments!!).gameName

        val adapter = BasicGamesListAdapter(GamesListListener { game ->
            viewModel.navigateToNewGameForm(game)
        })

        viewModel.getSearchResult(gameName)

        binding.lifecycleOwner = this
        binding.foundGamesList.apply {
            this.adapter = adapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
        binding.viewModel = viewModel

        binding.skip.setOnClickListener {
            navigateToNewGameForm(null)
        }

        viewModel.navigateToNewGameForm.observe(viewLifecycleOwner) { game ->
            game?.let {
                navigateToNewGameForm(game)
                viewModel.doneNavigating()
            }
        }

        return binding.root
    }

    private fun navigateToNewGameForm(game: MyGame?) {
        this.findNavController()
            .navigate(WebSearchResultFragmentDirections.navigateToNewGameForm(game, gameName))
    }

}