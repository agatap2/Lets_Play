package com.akobusinska.letsplay.ui.gamesList

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.CollectionOwner
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.repository.GameRepository
import com.akobusinska.letsplay.databinding.FragmentGamesListBinding
import com.akobusinska.letsplay.ui.gamesList.BasicGamesListAdapter.GamesListListener
import com.akobusinska.letsplay.utils.bindRecyclerView
import com.akobusinska.letsplay.utils.bindUsersDialogRecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GamesListFragment : Fragment() {

    private val FIND_USER_KEY = "find_user"
    private val viewModel: GamesListViewModel by viewModels()
    private lateinit var binding: FragmentGamesListBinding
    private var allSelected = true
    private var gamesSelected = false
    private var expansionsSelected = false
    private var refresh = true
    private var showStatus = true
    private var currentUser = "Default"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_games_list, container, false
        )

        val adapter = BasicGamesListAdapter(GamesListListener { game ->
            viewModel.navigateToGameDetails(game)
        })

        viewModel.status.observe(viewLifecycleOwner) { status ->
            if (status.equals(GameRepository.RequestStatus.DONE) && showStatus) {
                DialogSuccessFragment().show(
                    requireActivity().supportFragmentManager,
                    "success"
                )
                showStatus = false
            } else if (status.equals(GameRepository.RequestStatus.ERROR) && showStatus) {
                object : CountDownTimer(3000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                    }

                    override fun onFinish() {
                        DialogFailedFragment().show(
                            requireActivity().supportFragmentManager,
                            "failed"
                        )
                    }
                }.start()
                showStatus = false
            }
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            FIND_USER_KEY, viewLifecycleOwner
        ) { requestKey, bundle ->
            if (requestKey == FIND_USER_KEY) {
                val result = bundle.getString("bundleKey")
                if (result != null) {
                    viewModel.getUserCollection(result)
                }
            }
        }

        val changeCollectionDialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_users_list, null)

        var selectedUser: CollectionOwner? = null

        val usersListAdapter = DialogUsersListAdapter(DialogUsersListAdapter.UsersListListener {
            selectedUser = it
            currentUser = it.name
        })

        val listOfUsers = changeCollectionDialogView.findViewById<RecyclerView>(R.id.users_list)
        listOfUsers.apply {
            this.adapter = usersListAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }

        viewModel.users.observe(viewLifecycleOwner) { users ->
            var usersList = ""
            var ids = ""
            users.toSet()

            for (u in users) {
                usersList = usersList + u.name + ", "
                ids = ids + u.collectionOwnerId + ", "
            }

            listOfUsers.bindUsersDialogRecyclerView(users)
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.user -> {

                        if (changeCollectionDialogView.parent != null) (changeCollectionDialogView.parent as ViewGroup).removeView(
                            changeCollectionDialogView
                        )

                        MaterialAlertDialogBuilder(requireContext())
                            .setView(changeCollectionDialogView)
                            .setTitle(R.string.select_collection_owner)
                            .setPositiveButton(R.string.ok) { _, _ ->
                                if (selectedUser != null) {
                                    refresh = true
                                    if (expansionsSelected)
                                        viewModel.getGamesCollection(
                                            filtered = true,
                                            gameType = GameType.EXPANSION,
                                            ownerName = selectedUser!!.name
                                        )
                                    else if (gamesSelected)
                                        viewModel.getGamesCollection(
                                            filtered = true,
                                            gameType = GameType.GAME,
                                            ownerName = selectedUser!!.name
                                        )
                                    else
                                        viewModel.getGamesCollection(
                                            filtered = false,
                                            ownerName = selectedUser!!.name
                                        )
                                }
                            }
                            .setNegativeButton(R.string.cancel, null)
                            .show()

                    }

                    R.id.login -> {
                        val toast = Toast.makeText(requireContext(), "login", Toast.LENGTH_SHORT)
                        toast.show()
                    }

                    R.id.find_friend -> {
                        showStatus = true
                        DialogUserNameFragment().show(
                            requireActivity().supportFragmentManager,
                            DialogUserNameFragment::class.java.simpleName
                        )
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.fullGamesList.apply {
            this.adapter = adapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }

        binding.all.setOnClickListener {
            if (!allSelected)
                viewModel.getGamesCollection(false, name = getFilter(), ownerName = currentUser)

            allDisplayed()
        }

        binding.gameFilterButton.setOnClickListener {
            if (!gamesSelected)
                viewModel.getGamesCollection(
                    true,
                    GameType.GAME,
                    getFilter(),
                    ownerName = currentUser
                )

            onlyGamesDisplayed()
        }

        binding.expansionFilterButton.setOnClickListener {
            if (!expansionsSelected)
                viewModel.getGamesCollection(
                    true,
                    GameType.EXPANSION,
                    getFilter(),
                    ownerName = currentUser
                )

            onlyExpansionsDisplayed()
        }

        binding.addGameButton.setOnClickListener {

            DialogGameNameFragment().show(
                requireActivity().supportFragmentManager,
                "game_name"
            )
        }

        binding.selectGameButton.setOnClickListener {
            findNavController().navigate(GamesListFragmentDirections.navigateToGameSelectionFragment())
        }

        binding.searchBar.doOnTextChanged { text, _, _, _ ->
            viewModel.filterGames(text.toString())
            refresh = true
        }

        binding.searchBar

        viewModel.navigateToGameDetails.observe(viewLifecycleOwner) { game ->
            if (findNavController().currentDestination?.id == R.id.gamesListFragment) {
                this.findNavController().navigate(
                    GamesListFragmentDirections.navigateToGameDetails(game)
                )
                viewModel.doneNavigating()
            }
        }

        viewModel.gamesCollection.observe(viewLifecycleOwner) { gamesList ->
            if (refresh) {
                binding.fullGamesList.bindRecyclerView(gamesList.sortedBy { it.name })
                refresh = false
            }
        }

        return binding.root
    }

    private fun getFilter() = binding.searchBar.editableText.toString()

    private fun allDisplayed() {
        allSelected = true
        gamesSelected = false
        expansionsSelected = false
        refresh = true
    }

    private fun onlyGamesDisplayed() {
        allSelected = false
        gamesSelected = true
        expansionsSelected = false
        refresh = true
    }

    private fun onlyExpansionsDisplayed() {
        allSelected = false
        gamesSelected = false
        expansionsSelected = true
        refresh = true
    }
}