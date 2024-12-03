package com.akobusinska.letsplay.ui.gamesList

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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
import com.akobusinska.letsplay.data.local.CollectionOwnerWithGames
import com.akobusinska.letsplay.data.repository.GameRepository
import com.akobusinska.letsplay.databinding.FragmentGamesListBinding
import com.akobusinska.letsplay.ui.gamesList.BasicGamesListAdapter.GamesListListener
import com.akobusinska.letsplay.ui.gamesList.DialogUsersListAdapter.UsersListListener
import com.akobusinska.letsplay.utils.Keys
import com.akobusinska.letsplay.utils.Storage
import com.akobusinska.letsplay.utils.bindRecyclerView
import com.akobusinska.letsplay.utils.bindUsersDialogRecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GamesListFragment : Fragment() {

    private val viewModel: GamesListViewModel by viewModels()
    private lateinit var binding: FragmentGamesListBinding

    private var allSelected = true
    private var gamesSelected = false
    private var expansionsSelected = false
    private var newUser = "Default"
    private var selectedUser: CollectionOwner? = null
    private var selectedUserPosition = 0
    private var list = mutableListOf<CollectionOwnerWithGames>()
    private var newUserCreationProcess = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_games_list, container, false
        )

        checkIfCollectionIsEditable(
            Storage().restoreDefaultUser(requireContext())
        )

        val userExistsDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            .setMessage(resources.getString(R.string.user_already_exists))
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
            }

        val userExistsDialog: AlertDialog = userExistsDialogBuilder.create()

        requireActivity().supportFragmentManager.setFragmentResultListener(
            Keys.FIND_USER_KEY.key, viewLifecycleOwner
        ) { _, bundle ->
            val result = bundle.getString(Keys.FIND_USER_KEY.key)
            if (result != null) {
                if (!viewModel.checkIfUserExists(result)) {
                    newUser = result
                    newUserCreationProcess = true
                    viewModel.getUserCollection(result)
                } else {
                    userExistsDialog.show()
                }
            }
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            Keys.NEW_USER_KEY.key, viewLifecycleOwner
        ) { _, bundle ->
            val result = bundle.getBoolean(Keys.NEW_USER_KEY.key)
            if (result) {
                Storage().saveDefaultUser(requireContext(), newUser)
            }
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            Keys.CUSTOM_USER_NAME_KEY.key, viewLifecycleOwner
        ) { _, bundle ->
            val result = bundle.getString(Keys.CUSTOM_USER_NAME_KEY.key)
            if (result != null) {
                viewModel.updateUserCustomName(selectedUser, result)
            }
        }

        viewModel.statusOfNewUser.observe(viewLifecycleOwner) { status ->
            if (status.equals(GameRepository.RequestStatus.DONE) && newUserCreationProcess) {
                DialogSuccessFragment.newInstance(newUser).show(
                    requireActivity().supportFragmentManager, "success"
                )
                newUserCreationProcess = false
            } else if (status.equals(GameRepository.RequestStatus.ERROR)) {
                object : CountDownTimer(3000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                    }

                    override fun onFinish() {
                        DialogFailedFragment().show(
                            requireActivity().supportFragmentManager, "failed"
                        )
                    }
                }.start()
            }
        }

        viewModel.getUserByName(Storage().restoreCurrentUserName(requireContext()))
            .observe(viewLifecycleOwner) {
                updateCurrentUser(it)
            }

//        viewModel.getUserByName(Storage().restoreCurrentUserName(requireContext()))
//            .observe(viewLifecycleOwner) {
//                if (Storage().restoreCurrentUserName(requireContext()).isNotBlank()) {
//                    selectedUser = it
//                }
//            }

        viewModel.getLastCollectionOwner().observe(viewLifecycleOwner) {
            if (it != null) {
                if(newUserCreationProcess) {
                    updateCurrentUser(it)
                    viewModel.updateGamesList(it)
                }
                if(selectedUser?.name == it.name) {
                    selectedUser = it
                }
            }
        }

        viewModel.getCollectionOwnersWithGames().observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                list.clear()
                list.addAll(it)
                refreshCollection()
                try {
                    viewModel.stopLoadIcon(it.last().games.size == selectedUser!!.games.size && it.size > 1)
                } catch (e: Exception) {}
            }
        }

        val changeCollectionDialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_users_list, null)
        val listOfUsers = changeCollectionDialogView.findViewById<RecyclerView>(R.id.users_list)
        listOfUsers.addItemDecoration(
            DividerItemDecoration(
                this.context, DividerItemDecoration.VERTICAL
            )
        )

        viewModel.users.observe(viewLifecycleOwner) { users ->
            selectedUserPosition =
                users.indexOfFirst { it.name == (selectedUser?.name ?: "Default") }
            val usersListAdapter = DialogUsersListAdapter(UsersListListener {
                updateCurrentUser(it)
            }, selectedUserPosition)

            listOfUsers.adapter = usersListAdapter
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

                        MaterialAlertDialogBuilder(requireContext()).setView(
                            changeCollectionDialogView
                        ).setTitle(R.string.select_collection_owner)
                            .setPositiveButton(R.string.ok) { _, _ ->
                                if (selectedUser != null) {
                                    refreshCollection()
                                }
                            }.setNegativeButton(R.string.cancel, null).show()
                    }

                    R.id.bgg -> {
                        DialogUserNameFragment().show(
                            requireActivity().supportFragmentManager,
                            DialogUserNameFragment::class.java.simpleName
                        )
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)

        val adapter = BasicGamesListAdapter(GamesListListener { game ->
            viewModel.navigateToGameDetails(game)
        })

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.fullGamesList.apply {
            this.adapter = adapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }

        binding.all.setOnClickListener {
            if (!allSelected) {
                allDisplayed()
                refreshCollection()
            }
        }

        binding.gameFilterButton.setOnClickListener {
            if (!gamesSelected) {
                onlyGamesDisplayed()
                refreshCollection()
            }
        }

        binding.expansionFilterButton.setOnClickListener {
            if (!expansionsSelected) {
                onlyExpansionsDisplayed()
                refreshCollection()
            }
        }

        binding.addGameButton.setOnClickListener {
            DialogGameNameFragment().show(
                requireActivity().supportFragmentManager, "game_name"
            )
        }

        binding.selectGameButton.setOnClickListener {
            findNavController().navigate(
                GamesListFragmentDirections.navigateToGameSelectionFragment()
            )
        }

        binding.searchBar.doOnTextChanged { _, _, _, _ ->
            refreshCollection()
        }

        viewModel.navigateToGameDetails.observe(viewLifecycleOwner) { game ->
            if (findNavController().currentDestination?.id == R.id.gamesListFragment) {
                this.findNavController().navigate(
                    GamesListFragmentDirections.navigateToGameDetails(game)
                )
                viewModel.doneNavigating()
            }
        }

        viewModel.gamesCollection.observe(viewLifecycleOwner) { gamesList ->
            binding.fullGamesList.bindRecyclerView(gamesList.sortedBy { it.name })
        }

        return binding.root
    }

    private fun getFilter() = binding.searchBar.editableText.toString()

    private fun refreshCollection() {

        binding.collection.text = selectedUser?.customName

        if (expansionsSelected) viewModel.updateGamesCollection(
            GameType.EXPANSION, getFilter(), selectedUser?.collectionOwnerId ?: 1, list
        )
        else if (gamesSelected) viewModel.updateGamesCollection(
            GameType.GAME, getFilter(), selectedUser?.collectionOwnerId ?: 1, list
        )
        else viewModel.updateGamesCollection(
            null, getFilter(), selectedUser?.collectionOwnerId ?: 1, list
        )

        checkIfCollectionIsEditable(selectedUser?.name ?: "Default")
    }

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

    private fun checkIfCollectionIsEditable(userName: String) {
        if (userName != "Default") {
            binding.addGameButton.visibility = View.GONE
        } else {
            binding.addGameButton.visibility = View.VISIBLE
        }
    }

    private fun updateCurrentUser(currentUser: CollectionOwner) {
        selectedUser = currentUser
        Storage().saveCurrentUserName(requireContext(), currentUser.name)
        Storage().saveCurrentUserId(requireContext(), currentUser.collectionOwnerId)
    }
}