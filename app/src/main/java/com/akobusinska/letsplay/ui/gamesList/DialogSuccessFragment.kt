package com.akobusinska.letsplay.ui.gamesList


import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.databinding.DialogSuccessBinding
import com.akobusinska.letsplay.utils.Keys.CUSTOM_USER_NAME_KEY
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogSuccessFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = MaterialAlertDialogBuilder(requireContext())
        val binding: DialogSuccessBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.dialog_success, null, false)

        val alertDialog = builder
            .setView(binding.root)
            .setPositiveButton(R.string.ok) { _, _ ->
                requireActivity().supportFragmentManager.setFragmentResult(
                    CUSTOM_USER_NAME_KEY.key,
                    bundleOf(CUSTOM_USER_NAME_KEY.key to binding.userName.editText?.text.toString())
                )
            }
            .create()

        binding.successInformation.text =
            resources.getString(R.string.new_user_success, arguments?.getString("username"))

        binding.executePendingBindings()

        return alertDialog
    }

    companion object {
        fun newInstance(userName: String): DialogSuccessFragment {
            val instance = DialogSuccessFragment()
            val args = Bundle()
            args.putString("username", userName)
            instance.arguments = args
            return instance
        }
    }
}