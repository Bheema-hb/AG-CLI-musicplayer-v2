package com.harmonicplayer.app.ui.splash

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun PermissionRationaleDialog(
    title: String,
    text: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    dismissButtonText: String? = null,
    onDismiss: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = { onDismiss?.invoke() },
        title = { Text(text = title) },
        text = { Text(text = text) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmButtonText)
            }
        },
        dismissButton = if (dismissButtonText != null && onDismiss != null) {
            {
                TextButton(onClick = onDismiss) {
                    Text(text = dismissButtonText)
                }
            }
        } else null
    )
}
