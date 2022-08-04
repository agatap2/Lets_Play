package com.akobusinska.letsplay.utils

import android.app.AlertDialog
import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.doOnTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun TextInputEditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(editableText.toString())
        }

    })
}

fun MaterialAlertDialogBuilder.showInput(
    textInputId: Int,
    hintResId: Int
): Dialog {

    val dialog = this.show()
    val til = dialog.findViewById<TextInputLayout>(textInputId)
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
    til?.let {
        til.hint = context.getString(hintResId)
        til.editText?.doOnTextChanged { text, _, _, _ ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .isEnabled = !text.isNullOrBlank()
        }
    }
    return dialog
}