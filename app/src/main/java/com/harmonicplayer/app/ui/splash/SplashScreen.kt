package com.harmonicplayer.app.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            is SplashUiState.PermissionRequired -> {
                onRequestPermission()
            }
            is SplashUiState.Success -> {
                onNavigateToLibrary()
            }
            else -> {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = "HarmonicPlayer Branding Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(96.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "HarmonicPlayer",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your High-Fidelity Local Music Player",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = uiState is SplashUiState.Loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
                )
            }
        }

        when (uiState) {
            is SplashUiState.ShowRationale -> {
                PermissionRationaleDialog(
                    title = "Media Access Required",
                    text = "HarmonicPlayer requires permission to access audio files on your device in order to build your media catalog and enable audio playback.",
                    confirmButtonText = "Grant Access",
                    onConfirm = { viewModel.onGrantAccessClicked() },
                    dismissButtonText = "Cancel",
                    onDismiss = { viewModel.onRationaleDismissed() }
                )
            }
            is SplashUiState.ShowSettingsRedirect -> {
                PermissionRationaleDialog(
                    title = "Permission Required in Settings",
                    text = "Permission to access audio files was denied. Please enable storage or media permission in Android App Settings to continue using HarmonicPlayer.",
                    confirmButtonText = "Open Settings",
                    onConfirm = { onOpenSettings() },
                    dismissButtonText = "Cancel",
                    onDismiss = { viewModel.onRationaleDismissed() }
                )
            }
            else -> {}
        }
    }
}
