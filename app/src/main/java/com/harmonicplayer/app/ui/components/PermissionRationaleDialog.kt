package com.harmonicplayer.app.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.harmonicplayer.app.util.PermissionState

@Composable
fun PermissionRationaleDialog(
    permissionState: PermissionState,
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    when (permissionState) {
        is PermissionState.ShowRationale -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text(text = "Media Access Required") },
                text = {
                    Text(
                        text = "HarmonicPlayer requires permission to access your audio files to scan and play local music. Please grant access to continue."
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        onRequestPermission()
                    }) {
                        Text(text = "Grant Permission")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
        is PermissionState.PermanentlyDenied -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text(text = "Permission Permanently Denied") },
                text = {
                    Text(
                        text = "Media access is permanently disabled for HarmonicPlayer. Please enable media permissions in OS app settings to play music."
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        openAppSettings(context)
                        onDismiss()
                    }) {
                        Text(text = "Go to Settings")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
        else -> {
            // No dialog required for Granted or initial Denied state
        }
    }
}

private fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
