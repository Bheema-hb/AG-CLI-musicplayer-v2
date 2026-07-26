package com.harmonicplayer.app.util

import kotlinx.coroutines.flow.StateFlow

interface PermissionManager {
    val permissionState: StateFlow<PermissionState>
    fun getRequiredMediaPermission(): String
    fun checkPermissionStatus(): PermissionState
    fun onPermissionResult(isGranted: Boolean, shouldShowRationale: Boolean)
}
