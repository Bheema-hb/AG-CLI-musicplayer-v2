package com.harmonicplayer.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.harmonicplayer.app.ui.splash.SplashScreen
import com.harmonicplayer.app.ui.splash.SplashUiState
import com.harmonicplayer.app.ui.splash.SplashViewModel
import com.harmonicplayer.app.ui.theme.HarmonicPlayerTheme
import com.harmonicplayer.app.util.PermissionManager
import com.harmonicplayer.app.util.PermissionObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissionManager: PermissionManager

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissionObserver = PermissionObserver(permissionManager) { isGranted ->
            val currentState = splashViewModel.uiState.value
            if (!isGranted && currentState is SplashUiState.Success) {
                splashViewModel.onPermissionRevoked()
            } else if (isGranted && currentState !is SplashUiState.Success) {
                splashViewModel.checkPermissionStatus()
            }
        }
        lifecycle.addObserver(permissionObserver)

        setContent {
            HarmonicPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val requiredPermission = permissionManager.getRequiredPermission()
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        val shouldShowRationale = shouldShowRequestPermissionRationale(requiredPermission)
                        splashViewModel.onPermissionResult(isGranted, shouldShowRationale)
                    }

                    val uiState by splashViewModel.uiState.collectAsState()

                    if (uiState is SplashUiState.Success) {
                        LibraryScreen()
                    } else {
                        SplashScreen(
                            viewModel = splashViewModel,
                            onRequestPermission = {
                                permissionLauncher.launch(requiredPermission)
                            },
                            onOpenSettings = {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", packageName, null)
                                }
                                startActivity(intent)
                            },
                            onNavigateToLibrary = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Library Screen - Permission Granted",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
