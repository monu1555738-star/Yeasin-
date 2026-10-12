package com.example.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Phone
import com.example.data.ThemeMode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.example.ui.theme.CallDeclineRed
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsState()
    val blockedContacts by viewModel.blockedContacts.collectAsState()
    val userPreferencesConfig by viewModel.userPreferencesConfig.collectAsState()
    var showArchDialog by remember { mutableStateOf(false) }
    var showRingtonePicker by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Call Settings
            SettingsSectionHeader(title = "Call Settings")
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                    // Ringtone On/Off
                    SettingsToggleRow(
                        icon = Icons.Default.Phone,
                        title = "Incoming Call Ringtone",
                        subtitle = "Play audio ringtone for incoming calls",
                        checked = settings.ringtoneEnabled,
                        onCheckedChange = { viewModel.updateRingtoneEnabled(it) },
                        tag = "ringtone_toggle"
                    )

                    // Change Ringtone
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showRingtonePicker = true }
                            .testTag("change_ringtone_row"),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Ringtone Sound", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text(settings.ringtoneTitle, fontSize = 12.sp, color = SlateMuted)
                                }
                            }
                            Text("Change", color = TealPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Vibrate on call
                    SettingsToggleRow(
                        icon = Icons.Default.Vibration,
                        title = "Vibrate on Incoming Call",
                        subtitle = "Vibrate device for ringing calls",
                        checked = settings.vibrateEnabled,
                        onCheckedChange = { viewModel.updateVibrateEnabled(it) },
                        tag = "vibrate_toggle"
                    )
                }
            }

            // Section 2: Privacy & Messaging
            SettingsSectionHeader(title = "Privacy & Messaging")
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                    // Read Receipts
                    SettingsToggleRow(
                        icon = Icons.Default.DoneAll,
                        title = "Read Receipts",
                        subtitle = "Show double blue ticks when messages are read",
                        checked = settings.readReceiptsEnabled,
                        onCheckedChange = { viewModel.updateReadReceipts(it) },
                        tag = "read_receipts_toggle"
                    )

                    // Blocked Contacts
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.navigateTo(Screen.BLOCK_LIST) }
                            .testTag("settings_blocked_contacts_btn"),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Blocked Contacts", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("${blockedContacts.size} contacts blocked", fontSize = 12.sp, color = SlateMuted)
                                }
                            }
                            Text("Manage", color = TealPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Section: Appearance & Theme
            SettingsSectionHeader(title = "Appearance & Theme")
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showThemePicker = true }
                            .testTag("theme_selector_row"),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("App Theme Mode", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    val themeLabel = when (userPreferencesConfig.themeMode) {
                                        ThemeMode.SYSTEM -> "System Default"
                                        ThemeMode.LIGHT -> "Light Theme"
                                        ThemeMode.DARK -> "Dark Theme"
                                    }
                                    Text(themeLabel, fontSize = 12.sp, color = SlateMuted)
                                }
                            }
                            Text("Change", color = TealPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Section: Notification Alerts
            SettingsSectionHeader(title = "Notifications & Alerts")
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                    SettingsToggleRow(
                        icon = Icons.Default.Notifications,
                        title = "Message Notifications",
                        subtitle = "Alerts for incoming chat messages",
                        checked = userPreferencesConfig.messageNotifications,
                        onCheckedChange = { viewModel.setMessageNotifications(it) },
                        tag = "message_notifications_toggle"
                    )

                    SettingsToggleRow(
                        icon = Icons.Default.Phone,
                        title = "Call Notifications",
                        subtitle = "Alerts for incoming audio and video calls",
                        checked = userPreferencesConfig.callNotifications,
                        onCheckedChange = { viewModel.setCallNotifications(it) },
                        tag = "call_notifications_toggle"
                    )
                }
            }

            // Section: Backend & Architecture Documentation
            SettingsSectionHeader(title = "Infrastructure & Architecture")
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showArchDialog = true }
                        .testTag("open_arch_dialog_btn"),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Production Backend Services",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "WebSockets, WebRTC signaling, PostgreSQL, Twilio & FCM specs",
                                fontSize = 12.sp,
                                color = SlateMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Logout
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("settings_logout_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x22EF4444)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, tint = CallDeclineRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out", color = CallDeclineRed, fontWeight = FontWeight.Bold)
            }
        }

        // Ringtone Picker Dialog
        if (showRingtonePicker) {
            val ringtones = listOf(
                "Oceanic Pulse (Default)",
                "BSL CALLING Chime",
                "Crystal Wave",
                "Subtle Horizon"
            )
            AlertDialog(
                onDismissRequest = { showRingtonePicker = false },
                title = { Text("Choose Ringtone") },
                text = {
                    Column {
                        ringtones.forEach { name ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateRingtoneTitle(name)
                                        showRingtonePicker = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = name,
                                    fontSize = 15.sp,
                                    fontWeight = if (name == settings.ringtoneTitle) FontWeight.Bold else FontWeight.Normal,
                                    color = if (name == settings.ringtoneTitle) TealPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showRingtonePicker = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Theme Picker Dialog
        if (showThemePicker) {
            AlertDialog(
                onDismissRequest = { showThemePicker = false },
                title = { Text("Choose Theme Mode") },
                text = {
                    Column {
                        listOf(
                            ThemeMode.SYSTEM to "System Default",
                            ThemeMode.LIGHT to "Light Theme",
                            ThemeMode.DARK to "Dark Theme"
                        ).forEach { (mode, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setThemeMode(mode)
                                        showThemePicker = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 15.sp,
                                    fontWeight = if (userPreferencesConfig.themeMode == mode) FontWeight.Bold else FontWeight.Normal,
                                    color = if (userPreferencesConfig.themeMode == mode) TealPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemePicker = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showArchDialog) {
            ProductionArchitectureDialog(onDismiss = { showArchDialog = false })
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Log Out from BSL CALLING?") },
                text = { Text("Are you sure you want to log out?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            viewModel.logout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CallDeclineRed)
                    ) {
                        Text("Log Out")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = TealPrimary,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TealPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(text = subtitle, fontSize = 12.sp, color = SlateMuted)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = TealPrimary
            ),
            modifier = Modifier.testTag(tag)
        )
    }
}
