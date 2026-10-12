package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.notifications.AppNotificationHelper
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.OtpVerificationScreen
import com.example.ui.auth.ProfileSetupScreen
import com.example.ui.call.AudioCallScreen
import com.example.ui.call.VideoCallScreen
import com.example.ui.chat.PrivateChatScreen
import com.example.ui.home.HomeScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.settings.BlockListScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.story.StoryViewerScreen
import com.example.ui.theme.BEShellCallTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var notificationHelper: AppNotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        notificationHelper = AppNotificationHelper(applicationContext)

        setContent {
            val preferencesConfig by viewModel.userPreferencesConfig.collectAsState()
            val isSystemInDark = androidx.compose.foundation.isSystemInDarkTheme()
            val useDarkTheme = when (preferencesConfig.themeMode) {
                com.example.data.ThemeMode.DARK -> true
                com.example.data.ThemeMode.LIGHT -> false
                com.example.data.ThemeMode.SYSTEM -> isSystemInDark
            }

            BEShellCallTheme(darkTheme = useDarkTheme) {
                val snackbarHostState = remember { SnackbarHostState() }
                val currentScreen by viewModel.currentScreen.collectAsState()
                val toastMessage by viewModel.toastMessage.collectAsState()

                // Request runtime permissions for Audio and Camera
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { /* Permissions handled */ }

                LaunchedEffect(Unit) {
                    val permissionsNeeded = mutableListOf<String>()
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        permissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
                    }
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        permissionsNeeded.add(Manifest.permission.CAMERA)
                    }
                    if (permissionsNeeded.isNotEmpty()) {
                        permissionLauncher.launch(permissionsNeeded.toTypedArray())
                    }
                }

                LaunchedEffect(toastMessage) {
                    toastMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearToast()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            Screen.AUTH_LOGIN -> LoginScreen(viewModel = viewModel)
                            Screen.AUTH_OTP -> OtpVerificationScreen(viewModel = viewModel)
                            Screen.AUTH_PROFILE -> ProfileSetupScreen(viewModel = viewModel)
                            Screen.HOME -> HomeScreen(viewModel = viewModel)
                            Screen.CHAT_DETAIL -> PrivateChatScreen(viewModel = viewModel)
                            Screen.AUDIO_CALL -> AudioCallScreen(viewModel = viewModel)
                            Screen.VIDEO_CALL -> VideoCallScreen(viewModel = viewModel)
                            Screen.PROFILE -> ProfileScreen(viewModel = viewModel)
                            Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                            Screen.BLOCK_LIST -> BlockListScreen(viewModel = viewModel)
                            Screen.STORY_VIEWER -> StoryViewerScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
