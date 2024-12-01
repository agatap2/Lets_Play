package com.akobusinska.letsplay.utils

import android.app.AlertDialog
import android.app.Dialog
import android.widget.ImageButton
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.akobusinska.letsplay.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

fun MaterialAlertDialogBuilder.showInput(
    textInputId: Int,
    hintResId: Int,
    defaultText: String = ""
): Dialog {

    val dialog = this.show()
    val til = dialog.findViewById<TextInputLayout>(textInputId)
    if (defaultText.isBlank())
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
    til?.let {
        til.hint = context.getString(hintResId)
        til.editText?.setText(defaultText)
        til.editText?.doOnTextChanged { text, _, _, _ ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .isEnabled = !text.isNullOrBlank()
        }
    }
    return dialog
}

fun ImageButton.changeButtonColor(block: Boolean) {
    if (block)
        this.setColorFilter(context.resources.getColor(R.color.gray))
    else
        this.setColorFilter(context.resources.getColor(R.color.primaryColor))
}

fun Int.roundUp(): Int {
    val modulo = this.mod(5)
    return if (modulo != 0) (this + 5 - modulo)
    else this
}

fun Int.roundDown(): Int {
    val modulo = this.mod(5)
    return if (modulo != 0) (this - modulo)
    else this
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            try {
                observer.onChanged(value)
                removeObserver(this)
            } catch (e: Exception) {
            }
        }
    })
}