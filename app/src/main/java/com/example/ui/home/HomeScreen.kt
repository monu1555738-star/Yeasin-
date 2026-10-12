package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.common.ContactAvatar
import com.example.ui.settings.ProductionArchitectureDialog
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.HomeTab
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val currentTab by viewModel.currentHomeTab.collectAsState()
    val session by viewModel.session.collectAsState()
    val missedCalls by viewModel.missedCalls.collectAsState()
    val otherStories by viewModel.othersStories.collectAsState()
    var showArchDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (currentTab != HomeTab.UPDATES) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BSL CALLING",
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.PROFILE) },
                            modifier = Modifier.testTag("home_profile_button")
                        ) {
                            ContactAvatar(
                                name = session?.displayName ?: "Me",
                                avatarResName = session?.avatarResName ?: "avatar_alex",
                                size = 36.dp
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showArchDialog = true },
                            modifier = Modifier.testTag("backend_arch_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Backend Infrastructure",
                                tint = CyanSecondary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.SETTINGS) },
                            modifier = Modifier.testTag("home_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            // Exactly 3 tabs: 'Chats', 'Updates', 'Calls'
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // Tab 1: Chats
                NavigationBarItem(
                    selected = currentTab == HomeTab.CHATS,
                    onClick = { viewModel.setHomeTab(HomeTab.CHATS) },
                    icon = {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = "Chats")
                    },
                    label = { Text("Chats", fontWeight = if (currentTab == HomeTab.CHATS) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TealPrimary,
                        selectedTextColor = TealPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_chats_tab")
                )

                // Tab 2: Updates (Selected by default)
                NavigationBarItem(
                    selected = currentTab == HomeTab.UPDATES,
                    onClick = { viewModel.setHomeTab(HomeTab.UPDATES) },
                    icon = {
                        if (otherStories.isNotEmpty()) {
                            BadgedBox(badge = { Badge { Text("${otherStories.size}") } }) {
                                Icon(imageVector = Icons.Default.DonutLarge, contentDescription = "Updates")
                            }
                        } else {
                            Icon(imageVector = Icons.Default.DonutLarge, contentDescription = "Updates")
                        }
                    },
                    label = { Text("Updates", fontWeight = if (currentTab == HomeTab.UPDATES) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TealPrimary,
                        selectedTextColor = TealPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_updates_tab")
                )

                // Tab 3: Calls
                NavigationBarItem(
                    selected = currentTab == HomeTab.CALLS,
                    onClick = { viewModel.setHomeTab(HomeTab.CALLS) },
                    icon = {
                        if (missedCalls.isNotEmpty()) {
                            BadgedBox(badge = { Badge { Text("${missedCalls.count()}") } }) {
                                Icon(imageVector = Icons.Default.Call, contentDescription = "Calls")
                            }
                        } else {
                            Icon(imageVector = Icons.Default.Call, contentDescription = "Calls")
                        }
                    },
                    label = { Text("Calls", fontWeight = if (currentTab == HomeTab.CALLS) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TealPrimary,
                        selectedTextColor = TealPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_calls_tab")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                HomeTab.UPDATES -> UpdatesTab(
                    viewModel = viewModel,
                    onViewStory = { story -> viewModel.openStoryViewer(story) }
                )
                HomeTab.CHATS -> ChatsTab(
                    viewModel = viewModel,
                    onStartNewChat = { viewModel.showToast("Start new conversation") }
                )
                HomeTab.CALLS -> CallsTab(
                    viewModel = viewModel,
                    onStartNewCall = { viewModel.showToast("Choose contact to call") }
                )
            }
        }

        if (showArchDialog) {
            ProductionArchitectureDialog(onDismiss = { showArchDialog = false })
        }
    }
}
