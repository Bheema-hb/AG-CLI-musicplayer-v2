package com.harmonicplayer.app.util

sealed interface PermissionState {
    data object Granted : PermissionState
    data object Denied : PermissionState
    data class ShowRationale(val requiredPermission: String) : PermissionState
    data class PermanentlyDenied(val requiredPermission: String) : PermissionState
}
