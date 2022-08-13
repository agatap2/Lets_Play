package com.akobusinska.letsplay.ui.addGame

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.databinding.FragmentAddNewGameBinding
import com.akobusinska.letsplay.ui.addGame.DialogGamesListAdapter.GamesListListener
import com.akobusinska.letsplay.utils.bindDialogRecyclerView
import com.akobusinska.letsplay.utils.changeButtonColor
import com.akobusinska.letsplay.utils.setGameCover
import com.akobusinska.letsplay.utils.showInput
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.RangeSlider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewGameFragment : Fragment() {

    private lateinit var title: TextInputLayout
    private lateinit var type: GameType
    private lateinit var minNumberOfPlayers: TextView
    private lateinit var maxNumberOfPlayers: TextView
    private lateinit var numberOfPlayersSlider: RangeSlider
    private lateinit var minPlaytime: TextView
    private lateinit var maxPlaytime: TextView
    private lateinit var playtimeSlider: RangeSlider
    private lateinit var recommendedForMore: SwitchMaterial
    private lateinit var minAgeValue: TextView
    private lateinit var application: Application
    private lateinit var pictureUrl: String
    private var parent: Int = -1

    val viewModel: AddNewGameViewModel by viewModels()
    private val args: AddNewGameFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentAddNewGameBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_new_game, container, false)

        val game = args.game

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.getSearchResult()

        application = requireNotNull(activity).application
        pictureUrl = game?.thumbURL.toString()
        title = binding.titleLayout
        type = game?.gameType ?: GameType.GAME
        minNumberOfPlayers = binding.minNumOfPlayers
        maxNumberOfPlayers = binding.maxNumOfPlayers
        numberOfPlayersSlider = binding.numOfPlayersSlider
        minPlaytime = binding.minPlaytime
        maxPlaytime = binding.maxPlaytime
        playtimeSlider = binding.playtimeSlider
        recommendedForMore = binding.recommendedForMoreSwitch
        parent = game?.parentGame!!

        val moreThan20 = binding.moreThan20

        if (game.maxPlayers > 20) moreThan20.isChecked = true

        moreThan20.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                onOutOfRangeCheckboxClick(
                    "20+",
                    maxNumberOfPlayers,
                    numberOfPlayersSlider,
                    R.drawable.gray_square,
                    20.0F
                )
            else
                onOutOfRangeCheckboxClick(
                    numberOfPlayersSlider.values[1].toInt().toString(),
                    maxNumberOfPlayers,
                    numberOfPlayersSlider,
                    R.drawable.green_square,
                    numberOfPlayersSlider.values[1]
                )
        }

        numberOfPlayersSlider.setLabelFormatter { value ->
            if (value == 20.0F && moreThan20.isChecked) application.resources.getString(R.string.over_20) else value.toInt()
                .toString()
        }

        numberOfPlayersSlider.addOnChangeListener { slider, _, _ ->
            binding.minNumOfPlayers.text = slider.values[0].toInt().toString()

            if (!moreThan20.isChecked)
                maxNumberOfPlayers.text = slider.values[1].toInt().toString()

            if (slider.values[1] < 20)
                moreThan20.isChecked = false
        }

        val over2Hours = binding.moreThan2Hours

        if (game.maxPlaytime > 120) over2Hours.isChecked = true

        over2Hours.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                onOutOfRangeCheckboxClick(
                    "2h+",
                    maxPlaytime,
                    playtimeSlider,
                    R.drawable.gray_square,
                    120.0F
                )
            else
                onOutOfRangeCheckboxClick(
                    playtimeSlider.values[1].toInt().toString(),
                    maxPlaytime,
                    playtimeSlider,
                    R.drawable.green_square,
                    playtimeSlider.values[1]
                )
        }

        playtimeSlider.setLabelFormatter { value ->
            if (value == 120.0F && over2Hours.isChecked) application.resources.getString(R.string.over_2h) else value.toInt()
                .toString() + "min"
        }

        playtimeSlider.addOnChangeListener { slider, _, _ ->
            binding.minPlaytime.text = slider.values[0].toInt().toString()

            if (!over2Hours.isChecked)
                maxPlaytime.text = slider.values[1].toInt().toString()

            if (slider.values[1] < 120)
                over2Hours.isChecked = false
        }

        if (game.gameType == GameType.EXPANSION) {
            binding.gameType.check(R.id.expansionButton)
            binding.parentGame.visibility = View.VISIBLE
            binding.editParentGame.visibility = View.VISIBLE
        } else
            binding.gameType.check(R.id.gameButton)

        val parentGame = binding.parentGame
        val editParentGameButton = binding.editParentGame

        binding.gameType[0].setOnClickListener {
            parentGame.visibility = View.GONE
            editParentGameButton.visibility = View.GONE
            type = GameType.GAME
        }
        binding.gameType[1].setOnClickListener {
            parentGame.visibility = View.VISIBLE
            editParentGameButton.visibility = View.VISIBLE
            type = GameType.EXPANSION
        }

        val editParentGameDialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_games_list, null)

        var selectedGame: MyGame? = null

        val adapter = DialogGamesListAdapter(GamesListListener {
            selectedGame = it
        })

        val listOfGames = editParentGameDialogView.findViewById<RecyclerView>(R.id.games_list)
        listOfGames.adapter = adapter

        viewModel.foundGamesList.observe(viewLifecycleOwner) {
            listOfGames.bindDialogRecyclerView(it)
        }

        binding.editParentGame.setOnClickListener {
            if (editParentGameDialogView.parent != null) (editParentGameDialogView.parent as ViewGroup).removeView(
                editParentGameDialogView
            )

            MaterialAlertDialogBuilder(requireContext())
                .setView(editParentGameDialogView)
                .setTitle(R.string.select_parent_game)
                .setPositiveButton(R.string.ok) { _, _ ->
                    if (selectedGame != null) {
                        parent = selectedGame!!.id
                        viewModel.updateParentGame(selectedGame!!)
                        binding.parentGame.text = application.resources.getString(
                            R.string.parent_game,
                            selectedGame!!.name
                        )
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        minAgeValue = binding.minAgeValue
        val decreaseAge = binding.minusButton
        val increaseAge = binding.plusButton

        if (game.minAge == 3) decreaseAge.changeButtonColor(application, true)
        else if (game.minAge == 18) increaseAge.changeButtonColor(application, true)

        decreaseAge.setOnClickListener {
            var minAge = minAgeValue.text.toString().toInt()
            if (minAge > 3)
                minAge--
            if (minAge == 3) decreaseAge.changeButtonColor(application, true)
            else decreaseAge.changeButtonColor(application, false)

            increaseAge.changeButtonColor(application, false)
            minAgeValue.text = minAge.toString()
        }

        increaseAge.setOnClickListener {
            var maxAge = minAgeValue.text.toString().toInt()
            if (maxAge < 18)
                maxAge++
            if (maxAge == 18) increaseAge.changeButtonColor(application, true)
            else increaseAge.changeButtonColor(application, false)

            decreaseAge.changeButtonColor(application, false)
            minAgeValue.text = maxAge.toString()
        }

        val recommendedForMorePlayersText = binding.recommendedForMoreText

        binding.recommendedForMoreSwitch.setOnCheckedChangeListener { _, isChecked ->

            if (!isChecked) {
                recommendedForMorePlayersText.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.secondaryTextColor
                    )
                )
            } else {
                recommendedForMorePlayersText.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primaryTextColor
                    )
                )
            }
        }

        binding.editUrlButton.setOnClickListener {

            val editUrlDialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.dialog_text_input, null)

            MaterialAlertDialogBuilder(requireContext())
                .setView(editUrlDialogView)
                .setTitle(R.string.provide_cover_picture_url)
                .setPositiveButton(R.string.ok) { _, _ ->
                    pictureUrl =
                        editUrlDialogView.findViewById<TextInputLayout>(R.id.dialog_text_input_layout)?.editText?.text.toString()
                    binding.cover.setGameCover(pictureUrl)
                }
                .setNegativeButton(R.string.cancel, null)
                .showInput(
                    R.id.dialog_text_input_layout,
                    R.string.picture_url,
                    pictureUrl
                )
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(AddNewGameFragmentDirections.navigateToCollectionList())
                }
            }
        )

        binding.save.setOnClickListener {
            saveGame()
            navigateToMainScreen()
        }

        binding.cancel.setOnClickListener {
            navigateToMainScreen()
        }

        return binding.root
    }

    private fun navigateToMainScreen() {
        findNavController().navigate(AddNewGameFragmentDirections.navigateToCollectionList())
    }

    private fun onOutOfRangeCheckboxClick(
        text: String,
        textBox: TextView,
        slider: RangeSlider,
        background: Int,
        maxNumber: Float
    ) {
        textBox.text = text
        textBox.background = application.resources.getDrawable(background)
        slider.values =
            listOf(slider.values[0], maxNumber)
    }

    private fun saveGame() {
        viewModel.updateGameFields(
            title.editText?.text.toString(),
            numberOfPlayersSlider.values[0].toInt(),
            numberOfPlayersSlider.values[0].toInt(),
            recommendedForMore.isChecked,
            playtimeSlider.values[0].toInt(),
            playtimeSlider.values[1].toInt(),
            minAgeValue.text.toString().toInt(),
            pictureUrl,
            type,
            parent
        )

        viewModel.insertGameIntoDatabase()
    }
}