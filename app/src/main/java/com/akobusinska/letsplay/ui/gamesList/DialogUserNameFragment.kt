package com.akobusinska.letsplay.ui.gamesList


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.databinding.DialogTextFieldBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DialogUserNameFragment : DialogFragment() {

    private val FIND_USER_KEY = "find_user"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(context)
        val binding: DialogTextFieldBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_text_field, null, false)

        val alertDialog = builder
            .setView(binding.root)
            .setTitle(resources.getString(R.string.user_name))
            .setPositiveButton(R.string.ok) { _, _ ->
                requireActivity().supportFragmentManager.setFragmentResult(
                    FIND_USER_KEY,
                    bundleOf("bundleKey" to binding.dialogTextInputLayout.editText?.text.toString())
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .create()

        binding.dialogTextInputLayout.hint = resources.getString(R.string.game_name)

        binding.dialogTextInputLayout.editText?.let {
            it.doOnTextChanged { text, _, _, _ ->
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = !text.isNullOrBlank()
            }
        }

        binding.executePendingBindings()

        return alertDialog
    }
}