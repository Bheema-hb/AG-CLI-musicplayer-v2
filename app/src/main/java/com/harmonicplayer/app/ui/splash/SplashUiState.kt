package com.harmonicplayer.app.ui.splash

sealed interface SplashUiState {
    data object Loading : SplashUiState
    data object PermissionRequired : SplashUiState
    data object ShowRationale : SplashUiState
    data object ShowSettingsRedirect : SplashUiState
    data object Success : SplashUiState
}
