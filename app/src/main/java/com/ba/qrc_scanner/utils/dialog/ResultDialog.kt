package com.ba.qrc_scanner.utils.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.ba.qrc_scanner.R
import androidx.core.graphics.drawable.toDrawable

class ResultDialog(private val context: Context) {
    private var dialog: Dialog? = null

    fun showSuccess(
        title: String = "Success!",
        message: String,
        buttonText: String = "OK",
        onDismiss: (() -> Unit)? = null
    ) {
        showDialog(
            type = DialogType.SUCCESS,
            title = title,
            message = message,
            buttonText = buttonText,
            onDismiss = onDismiss
        )
    }

    fun showError(
        title: String = "Error",
        message: String,
        buttonText: String = "Try Again",
        onDismiss: (() -> Unit)? = null
    ) {
        showDialog(
            type = DialogType.ERROR,
            title = title,
            message = message,
            buttonText = buttonText,
            onDismiss = onDismiss
        )
    }

    private fun showDialog(
        type: DialogType,
        title: String,
        message: String,
        buttonText: String,
        onDismiss: (() -> Unit)?
    ) {
        dialog?.dismiss()

        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            setCancelable(false)

            val view = createDialogView(type, title, message, buttonText, onDismiss)
            setContentView(view)
        }

        dialog?.show()
    }

    private fun createDialogView(
        type: DialogType,
        title: String,
        message: String,
        buttonText: String,
        onDismiss: (() -> Unit)?
    ): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_result, null)

        val cardView = view.findViewById<CardView>(R.id.cardView)
        val iconImageView = view.findViewById<ImageView>(R.id.iconImageView)
        val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
        val messageTextView = view.findViewById<TextView>(R.id.messageTextView)
        val actionButton = view.findViewById<Button>(R.id.actionButton)

        when (type) {
            DialogType.SUCCESS -> {
                iconImageView.setImageResource(R.drawable.ic_success)
                iconImageView.setColorFilter(ContextCompat.getColor(context, R.color.success_color))
                actionButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.success_color))
            }
            DialogType.ERROR -> {
                iconImageView.setImageResource(R.drawable.ic_error)
                iconImageView.setColorFilter(ContextCompat.getColor(context, R.color.error_color))
                actionButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.error_color))
            }
        }

        titleTextView.text = title
        messageTextView.text = message
        actionButton.text = buttonText

        actionButton.setOnClickListener {
            dialog?.dismiss()
            onDismiss?.invoke()
        }

        return view
    }

    fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }

    private enum class DialogType {
        SUCCESS, ERROR
    }
}

