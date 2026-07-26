package com.harmonicplayer.app.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class PermissionObserver(
    private val permissionManager: PermissionManager,
    private val onPermissionStatusChecked: (Boolean) -> Unit
) : DefaultLifecycleObserver {
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        val isGranted = permissionManager.isPermissionGranted()
        onPermissionStatusChecked(isGranted)
    }
}
