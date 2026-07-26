package com.harmonicplayer.app.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PermissionManager {

    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.Denied)
    override val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    override fun getRequiredMediaPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    override fun checkPermissionStatus(): PermissionState {
        val requiredPermission = getRequiredMediaPermission()
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            requiredPermission
        ) == PackageManager.PERMISSION_GRANTED

        val state = if (isGranted) {
            PermissionState.Granted
        } else {
            PermissionState.Denied
        }
        _permissionState.value = state
        return state
    }

    override fun onPermissionResult(isGranted: Boolean, shouldShowRationale: Boolean) {
        val requiredPermission = getRequiredMediaPermission()
        val newState = when {
            isGranted -> PermissionState.Granted
            shouldShowRationale -> PermissionState.ShowRationale(requiredPermission)
            else -> PermissionState.PermanentlyDenied(requiredPermission)
        }
        _permissionState.value = newState
    }
}
