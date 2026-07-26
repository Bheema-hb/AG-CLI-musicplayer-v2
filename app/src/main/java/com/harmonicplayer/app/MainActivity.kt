package com.harmonicplayer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.harmonicplayer.app.ui.components.PermissionRationaleDialog
import com.harmonicplayer.app.ui.theme.HarmonicPlayerTheme
import com.harmonicplayer.app.util.LifecycleSecurityObserver
import com.harmonicplayer.app.util.PermissionManager
import com.harmonicplayer.app.util.PermissionState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var lifecycleSecurityObserver: LifecycleSecurityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleSecurityObserver.attachActivity(this)

        setContent {
            HarmonicPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        permissionManager = permissionManager
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleSecurityObserver.detachActivity(this)
    }
}

@Composable
fun MainScreen(
    permissionManager: PermissionManager
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? ComponentActivity
    val permissionState by permissionManager.permissionState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val requiredPermission = permissionManager.getRequiredMediaPermission()
        val shouldShowRationale = activity?.shouldShowRequestPermissionRationale(requiredPermission) ?: false
        permissionManager.onPermissionResult(isGranted, shouldShowRationale)
        showDialog = !isGranted
    }

    LaunchedEffect(Unit) {
        val currentStatus = permissionManager.checkPermissionStatus()
        if (currentStatus is PermissionState.Denied) {
            val requiredPerm = permissionManager.getRequiredMediaPermission()
            permissionLauncher.launch(requiredPerm)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "HarmonicPlayer",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        when (permissionState) {
            is PermissionState.Granted -> {
                Text(
                    text = "Status: Permission Granted. Ready for audio playback.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            is PermissionState.Denied -> {
                Text(
                    text = "Status: Permission Required.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    val requiredPerm = permissionManager.getRequiredMediaPermission()
                    permissionLauncher.launch(requiredPerm)
                }) {
                    Text(text = "Scan Media")
                }
            }
            is PermissionState.ShowRationale -> {
                Text(
                    text = "Status: Permission Rationale Needed.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showDialog = true }) {
                    Text(text = "View Permission Info")
                }
            }
            is PermissionState.PermanentlyDenied -> {
                Text(
                    text = "Status: Permission Permanently Denied.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showDialog = true }) {
                    Text(text = "Open Settings Instructions")
                }
            }
        }
    }

    if (showDialog && (permissionState is PermissionState.ShowRationale || permissionState is PermissionState.PermanentlyDenied)) {
        PermissionRationaleDialog(
            permissionState = permissionState,
            onRequestPermission = {
                showDialog = false
                val requiredPerm = permissionManager.getRequiredMediaPermission()
                permissionLauncher.launch(requiredPerm)
            },
            onDismiss = { showDialog = false }
        )
    }
}
