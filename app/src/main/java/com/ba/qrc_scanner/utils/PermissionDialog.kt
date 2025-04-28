package com.ba.qrc_scanner.utils

import android.app.AlertDialog
import android.content.Context

fun showPermissionDialog(
    context: Context,
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit
) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Permission required")
    builder.setMessage(
        permissionTextProvider.getDescription(isPermanentlyDeclined)
    )
    builder.setCancelable(true)

    builder.setPositiveButton(
        if (isPermanentlyDeclined) "Grant permission" else "OK"
    ) { dialog, _ ->
        if (isPermanentlyDeclined) {
            onGoToAppSettingsClick()
        } else {
            onOkClick()
        }
        dialog.dismiss()
    }

    builder.setOnDismissListener {
        onDismiss()
    }

    builder.show()
}
