package com.harmonicplayer.app.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

sealed interface PermissionState {
    data object Granted : PermissionState
    data object Denied : PermissionState
    data object ShowRationale : PermissionState
    data object PermanentlyDenied : PermissionState
}

@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getRequiredPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_AUDIO
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            getRequiredPermission()
        ) == PackageManager.PERMISSION_GRANTED
    }
}
