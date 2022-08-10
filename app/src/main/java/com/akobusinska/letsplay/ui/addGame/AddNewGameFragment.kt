package com.akobusinska.letsplay.ui.addGame

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.databinding.FragmentAddNewGameBinding
import com.akobusinska.letsplay.utils.changeButtonColor
import com.google.android.material.slider.RangeSlider


class AddNewGameFragment : Fragment() {

    private lateinit var maxNumberOfPlayers: TextView
    private lateinit var numberOfPlayersSlider: RangeSlider
    private lateinit var application: Application

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        application = requireNotNull(activity).application
        val binding: FragmentAddNewGameBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_new_game, container, false)

        val game = AddNewGameFragmentArgs.fromBundle(arguments!!).game

        val viewModelFactory = AddNewGameViewModelFactory(game)
        val viewModel = ViewModelProvider(this, viewModelFactory)[AddNewGameViewModel::class.java]

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        maxNumberOfPlayers = binding.maxNumOfPlayers
        numberOfPlayersSlider = binding.numOfPlayersSlider

        val moreThan20 = binding.moreThan100

        if (game?.maxPlayers!! > 20) moreThan20.isChecked = true

        moreThan20.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                onOver20PlayersCheckboxClick("20+", R.drawable.gray_square, 20.0F)
            else
                onOver20PlayersCheckboxClick(
                    numberOfPlayersSlider.values[1].toInt().toString(),
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

        val playTimeSlider = binding.playtimeSlider

        playTimeSlider.setLabelFormatter { value ->
            value.toInt().toString() + "min"
        }

        playTimeSlider.addOnChangeListener { slider, _, _ ->
            binding.minPlaytime.text = slider.values[0].toInt().toString()
            binding.maxPlaytime.text = slider.values[1].toInt().toString()
        }

        if (game.gameType == GameType.EXPANSION)
            binding.gameType.check(R.id.expansionButton)
        else
            binding.gameType.check(R.id.gameButton)

        val minAgeValue = binding.minAgeValue
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

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(AddNewGameFragmentDirections.navigateToCollectionList())
                }
            }
        )

        return binding.root
    }

    private fun onOver20PlayersCheckboxClick(
        text: String,
        background: Int,
        maxNumber: Float
    ) {
        maxNumberOfPlayers.text = text
        maxNumberOfPlayers.background = application.resources.getDrawable(background)
        numberOfPlayersSlider.values =
            listOf(numberOfPlayersSlider.values[0], maxNumber)
    }
}