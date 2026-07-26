package com.harmonicplayer.app.util

import android.app.Activity
import android.view.WindowManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.harmonicplayer.app.data.local.SecureStorageManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Process-wide lifecycle security observer that enforces screen privacy flags (FLAG_SECURE)
 * during background transition and sanitizes sensitive in-memory cached state.
 */
@Singleton
class LifecycleSecurityObserver @Inject constructor(
    private val secureStorageManager: SecureStorageManager
) : DefaultLifecycleObserver {

    private var currentActivity: Activity? = null
    private var isAppInBackground: Boolean = false

    fun attachActivity(activity: Activity) {
        this.currentActivity = activity
        applySecureWindowFlag(secure = true)
    }

    fun detachActivity(activity: Activity) {
        if (this.currentActivity == activity) {
            this.currentActivity = null
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        isAppInBackground = false
        applySecureWindowFlag(secure = true)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        applySecureWindowFlag(secure = true)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        isAppInBackground = true
        // Apply window screen privacy protection on backgrounding to prevent thumbnail leakage in recents
        applySecureWindowFlag(secure = true)
        // Sanitize sensitive transient in-memory session state
        sanitizeSensitiveState()
    }

    private fun applySecureWindowFlag(secure: Boolean) {
        currentActivity?.window?.let { window ->
            if (secure) {
                window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }

    private fun sanitizeSensitiveState() {
        secureStorageManager.remove("ephemeral_session_token")
    }

    fun isInBackground(): Boolean = isAppInBackground
}
