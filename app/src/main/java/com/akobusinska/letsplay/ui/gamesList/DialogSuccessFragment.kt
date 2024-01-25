package com.akobusinska.letsplay.ui.gamesList


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.databinding.DialogSuccessBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DialogSuccessFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(context)
        val binding: DialogSuccessBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_success, null, false)

        val alertDialog = builder
            .setView(binding.root)
            .setPositiveButton(R.string.ok) { _, _ ->
            }
            .create()

        binding.successInformation.text = "User added to friends list"
        binding.executePendingBindings()

        return alertDialog
    }
}