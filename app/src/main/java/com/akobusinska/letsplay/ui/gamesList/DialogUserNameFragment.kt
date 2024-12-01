package com.akobusinska.letsplay.ui.gamesList


import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.databinding.DialogTextFieldBinding
import com.akobusinska.letsplay.utils.Keys.FIND_USER_KEY
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DialogUserNameFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = MaterialAlertDialogBuilder(requireContext())
        val binding: DialogTextFieldBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.dialog_text_field, null, false)

        val alertDialog = builder
            .setView(binding.root)
            .setTitle(resources.getString(R.string.user_name))
            .setPositiveButton(R.string.ok) { _, _ ->
                requireActivity().supportFragmentManager.setFragmentResult(
                    FIND_USER_KEY.key,
                    bundleOf(FIND_USER_KEY.key to binding.dialogTextInputLayout.editText?.text.toString())
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