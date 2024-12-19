package com.akobusinska.letsplay.ui.gamesList

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
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
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
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
    private lateinit var searchBarItem: TextInputLayout
    private var filter = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_games_list, container, false
        )

        searchBarItem = binding.searchBarLayout

        checkIfCollectionIsEditable(
            Storage().restoreCurrentUserName(requireContext())
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
                if (selectedUser == null) {
                    updateCurrentUser(it)
                    binding.refresh.visibility =
                        if (it == null || it.name == "Default") View.INVISIBLE else View.VISIBLE
                }
            }

        viewModel.getLastCollectionOwner().observe(viewLifecycleOwner) {
            if (it != null) {
                if (newUserCreationProcess) {
                    updateCurrentUser(it)
                    viewModel.updateGamesList(it)
                }
                if (selectedUser?.name == it.name) {
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
                } catch (e: Exception) {
                }
            }
        }

        viewModel.statusOfRefresh.observe(viewLifecycleOwner) {
            if (it == GameRepository.RequestStatus.DONE) {
                selectedUser?.let { it1 -> viewModel.updateGamesList(it1, true) }
            }
        }

        val changeCollectionDialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_users_list, null)

        val usersListDialog = MaterialAlertDialogBuilder(requireContext()).setView(
            changeCollectionDialogView
        ).setTitle(R.string.select_collection_owner)
            .setPositiveButton(R.string.ok) { _, _ ->
                if (selectedUser != null) {
                    refreshCollection()
                    binding.refresh.visibility =
                        if (selectedUser?.name == "Default") View.INVISIBLE else View.VISIBLE
                }
            }.setNegativeButton(R.string.cancel, null)
            .setCancelable(true)

        val usersListAlertDialog = usersListDialog.create()

        changeCollectionDialogView.findViewById<ConstraintLayout>(R.id.add_new_user)
            .setOnClickListener {
                usersListAlertDialog.cancel()
                DialogUserNameFragment().show(
                    requireActivity().supportFragmentManager,
                    DialogUserNameFragment::class.java.simpleName
                )
            }
        val listOfUsers = changeCollectionDialogView.findViewById<RecyclerView>(R.id.users_list)
        listOfUsers.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            ).apply {
                isLastItemDecorated = false
            }
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
                    R.id.search -> {
                        if (binding.searchBarLayout.visibility == View.GONE) {
                            binding.searchBarLayout.visibility = View.VISIBLE
                        } else {
                            closeSearchBar()
                        }
                    }

                    R.id.user -> {
                        usersListAlertDialog.show()
                        closeSearchBar()
                    }

                    R.id.info -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(resources.getString(R.string.info))
                            .setMessage(resources.getString(R.string.version_number, "2.5.3"))
                            .setPositiveButton(resources.getString(R.string.ok)) { _, _ -> }
                            .show()
                        closeSearchBar()
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

        binding.refresh.setOnClickListener {
            viewModel.reDownloadUserCollection(selectedUser?.name ?: "Default")
        }

        binding.fullGamesList.apply {
            this.adapter = adapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }

        binding.filteringTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        if (!allSelected) {
                            allDisplayed()
                            refreshCollection()
                        }
                    }

                    1 -> {
                        if (!gamesSelected) {
                            onlyGamesDisplayed()
                            refreshCollection()
                        }
                    }

                    2 -> {
                        if (!expansionsSelected) {
                            onlyExpansionsDisplayed()
                            refreshCollection()
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

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

        binding.searchBar.doAfterTextChanged { text ->
            filter = text.toString()
            refreshCollection()
            closeSearchBar()

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
            binding.fullGamesList.layoutManager?.scrollToPosition(0)
            binding.fullGamesList.bindRecyclerView(gamesList.sortedBy { it.name })
        }

        return binding.root
    }

    private fun getFilter() = binding.searchBar.editableText.toString()

    private fun refreshCollection() {

        binding.collection.text = if (selectedUser?.collectionOwnerId == 1L) {
            selectedUser?.customName
        } else {
            getString(R.string.collection, selectedUser?.customName)
        }

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

    private fun closeSearchBar() {
        if (searchBarItem.visibility == View.VISIBLE && filter.isBlank()) {
            searchBarItem.visibility = View.GONE
            (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                binding.searchBar.windowToken,
                0
            )
        }
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

    private fun updateCurrentUser(currentUser: CollectionOwner?) {
        selectedUser = currentUser
        Storage().saveCurrentUserName(requireContext(), currentUser?.name ?: "Default")
        Storage().saveCurrentUserId(requireContext(), currentUser?.collectionOwnerId ?: 1)
    }
}