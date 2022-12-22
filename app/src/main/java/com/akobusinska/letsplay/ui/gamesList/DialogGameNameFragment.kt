package com.akobusinska.letsplay.ui.gamesList


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.databinding.DialogGameNameBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogGameNameFragment(private val title: String, private val label: String) :
    DialogFragment() {

    private lateinit var alertDialog: AlertDialog
    private var editText: EditText? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(context)
        val binding: DialogGameNameBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_game_name, null, false)

        alertDialog = builder
            .setView(binding.root)
            .setTitle(title)
            .setPositiveButton(R.string.ok) { _, _ ->
                this.findNavController().navigate(
                    GamesListFragmentDirections.searchForGame(
                        binding.dialogTextInputLayout.editText?.text.toString()
                    )
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .create()

        editText = binding.dialogTextInputLayout.editText

        editText?.let {
            it.hint = label
            it.doOnTextChanged { text, _, _, _ ->
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .isEnabled = !text.isNullOrBlank()
            }
        }

        binding.executePendingBindings()

        return alertDialog
    }

    override fun onStart() {
        super.onStart()
        if (editText?.text.toString() == "")
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
    }
}