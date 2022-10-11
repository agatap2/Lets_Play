package com.akobusinska.letsplay.ui.gameSelection


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.databinding.DialogGameFiltersBinding
import com.akobusinska.letsplay.utils.changeButtonColor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogGamesFilteringFragment(val viewModel: SelectGameViewModel) : DialogFragment() {

    private val filter = viewModel.currentFilter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(context)
        val binding: DialogGameFiltersBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_game_filters, null, false)

        val numberOfPlayersText = binding.numOfPlayersValue
        val numberOfPlayersSlider = binding.numOfPlayersSlider
        val maxPlaytime = binding.playtimeValue
        val maxPlaytimeSlider = binding.playtimeSlider
        val moreThan20 = binding.moreThan20
        val noTimeLimit = binding.noTimeLimit
        val minAgeText = binding.minAgeValue
        val decreaseAge = binding.minusButton
        val increaseAge = binding.plusButton
        val excludeRecommendedForMore = binding.excludeRecommendedForMore

        val twoHoursPlus = "2h+"
        val twentyPlayersPlus = "20+"

        val alertDialog = builder
            .setView(binding.root)
            .setTitle(R.string.filter_games)
            .setPositiveButton(R.string.filter) { _, _ ->
                viewModel.filterGamesCollection(filter)
            }
            .setNegativeButton(R.string.cancel, null)
            .create()

        if (filter.numberOfPlayers.toFloat() > 20) numberOfPlayersSlider.value = 20F
        else numberOfPlayersSlider.value = filter.numberOfPlayers.toFloat()

        if (filter.maxPlaytime.toFloat() > 120F) maxPlaytimeSlider.value = 120F
        else if (filter.maxPlaytime.toFloat() < 5F) maxPlaytimeSlider.value = 5F
        else maxPlaytimeSlider.value = filter.maxPlaytime.toFloat()

        if (filter.maxPlaytime > 120) {
            noTimeLimit.isChecked = true
            maxPlaytime.text = twoHoursPlus
        } else maxPlaytime.text = maxPlaytimeSlider.value.toInt().toString()

        if (filter.numberOfPlayers > 20) {
            moreThan20.isChecked = true
            numberOfPlayersText.text = twentyPlayersPlus
        } else numberOfPlayersText.text = numberOfPlayersSlider.value.toInt().toString()

        minAgeText.text = filter.age.toString()

        excludeRecommendedForMore.isChecked = filter.excludeRecommendedForMore

        when (filter.age) {
            3 -> {
                increaseAge.changeButtonColor(false)
                decreaseAge.changeButtonColor(true)
            }
            18 -> {
                increaseAge.changeButtonColor(true)
                decreaseAge.changeButtonColor(false)
            }
            else -> {
                increaseAge.changeButtonColor(false)
                decreaseAge.changeButtonColor(false)
            }
        }

        moreThan20.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                onOutOfRangeCheckboxClick(
                    twentyPlayersPlus,
                    numberOfPlayersText,
                    numberOfPlayersSlider,
                    R.drawable.gray_square,
                    20.0F
                )
                filter.numberOfPlayers = 100
            } else {
                onOutOfRangeCheckboxClick(
                    numberOfPlayersSlider.value.toInt().toString(),
                    numberOfPlayersText,
                    numberOfPlayersSlider,
                    R.drawable.green_square,
                    numberOfPlayersSlider.value
                )
                filter.numberOfPlayers = numberOfPlayersSlider.value.toInt()
            }
        }

        numberOfPlayersSlider.setLabelFormatter { value ->
            if (value == 20.0F && moreThan20.isChecked) requireContext().resources.getString(R.string.over_20) else value.toInt()
                .toString()
        }

        numberOfPlayersSlider.addOnChangeListener { slider, _, _ ->
            if (!moreThan20.isChecked) {
                numberOfPlayersText.text = slider.value.toInt().toString()
                filter.numberOfPlayers = numberOfPlayersSlider.value.toInt()
            }

            if (slider.value < 20)
                moreThan20.isChecked = false
        }

        noTimeLimit.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                onOutOfRangeCheckboxClick(
                    twoHoursPlus,
                    maxPlaytime,
                    maxPlaytimeSlider,
                    R.drawable.gray_square,
                    120.0F
                )
                filter.maxPlaytime = 480
            } else {
                onOutOfRangeCheckboxClick(
                    maxPlaytimeSlider.value.toInt().toString(),
                    maxPlaytime,
                    maxPlaytimeSlider,
                    R.drawable.green_square,
                    maxPlaytimeSlider.value
                )
                filter.maxPlaytime = maxPlaytimeSlider.value.toInt()
            }
        }

        maxPlaytimeSlider.setLabelFormatter { value ->
            if (value == 120.0F && noTimeLimit.isChecked) requireContext().resources.getString(R.string.over_2h) else value.toInt()
                .toString() + "min"
        }

        maxPlaytimeSlider.addOnChangeListener { slider, _, _ ->
            if (!noTimeLimit.isChecked) {
                maxPlaytime.text = slider.value.toInt().toString()
                filter.maxPlaytime = maxPlaytimeSlider.value.toInt()
            }

            if (slider.value < 120)
                noTimeLimit.isChecked = false
        }

        decreaseAge.setOnClickListener {
            var minAge = minAgeText.text.toString().toInt()
            if (minAge > 3)
                minAge--
            if (minAge == 3) decreaseAge.changeButtonColor(true)
            else decreaseAge.changeButtonColor(false)

            increaseAge.changeButtonColor(false)
            minAgeText.text = minAge.toString()
            filter.age = minAge
        }

        increaseAge.setOnClickListener {
            var maxAge = minAgeText.text.toString().toInt()
            if (maxAge < 18)
                maxAge++
            if (maxAge == 18) increaseAge.changeButtonColor(true)
            else increaseAge.changeButtonColor(false)

            decreaseAge.changeButtonColor(false)
            minAgeText.text = maxAge.toString()
            filter.age = maxAge
        }

        excludeRecommendedForMore.setOnCheckedChangeListener { _, isChecked ->
            filter.excludeRecommendedForMore = isChecked
        }

        binding.executePendingBindings()

        return alertDialog
    }

    private fun onOutOfRangeCheckboxClick(
        text: String,
        textBox: TextView,
        slider: Slider,
        background: Int,
        maxNumber: Float
    ) {
        textBox.text = text
        textBox.background = requireContext().resources.getDrawable(background)
        slider.value = maxNumber
    }
}