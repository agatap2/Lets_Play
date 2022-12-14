package com.akobusinska.letsplay.ui.gameSelection

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.databinding.DialogRandomGameBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogRandomGameFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val viewModel: SelectGameViewModel by activityViewModels()
        val selectedGame = viewModel.randomGame

        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(context)
        val binding: DialogRandomGameBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_random_game, null, false)

        binding.game = selectedGame

        val alertDialog = builder
            .setView(binding.root)
            .setTitle(getString(R.string.selected_random_game))
            .setPositiveButton(R.string.ok, null)
            .create()

        binding.executePendingBindings()

        return alertDialog
    }
}