package com.akobusinska.letsplay.ui.gamesList


import android.app.Dialog
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.databinding.DialogFailedBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DialogFailedFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = MaterialAlertDialogBuilder(requireContext())
        val binding: DialogFailedBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_failed, null, false)

        val alertDialog = builder
            .setView(binding.root)
            .setPositiveButton(R.string.ok) { _, _ ->
            }
            .create()

        binding.failedInformation.text = resources.getString(R.string.user_not_found)
        binding.executePendingBindings()

        return alertDialog
    }
}