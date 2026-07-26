package com.harmonicplayer.app.ui.splash

import androidx.lifecycle.ViewModel
import com.harmonicplayer.app.util.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkPermissionStatus()
    }

    fun checkPermissionStatus() {
        if (permissionManager.isPermissionGranted()) {
            _uiState.value = SplashUiState.Success
        } else {
            _uiState.value = SplashUiState.PermissionRequired
        }
    }

    fun onPermissionResult(isGranted: Boolean, shouldShowRationale: Boolean) {
        if (isGranted) {
            _uiState.value = SplashUiState.Success
        } else if (shouldShowRationale) {
            _uiState.value = SplashUiState.ShowRationale
        } else {
            _uiState.value = SplashUiState.ShowSettingsRedirect
        }
    }

    fun onGrantAccessClicked() {
        _uiState.value = SplashUiState.PermissionRequired
    }

    fun onRationaleDismissed() {
        _uiState.value = SplashUiState.PermissionRequired
    }

    fun onOpenSettingsClicked() {
        _uiState.value = SplashUiState.ShowSettingsRedirect
    }

    fun onPermissionRevoked() {
        // Halt media access and reset state back to permission required
        _uiState.value = SplashUiState.PermissionRequired
    }
}
