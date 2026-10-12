package com.example.ui.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.ui.common.ContactAvatar
import com.example.ui.theme.CallDeclineRed
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    val session by viewModel.session.collectAsState()
    val allCalls by viewModel.recentCalls.collectAsState()
    val allMessages by viewModel.allMessages.collectAsState()

    var isEditingName by remember { mutableStateOf(false) }
    var editedName by remember(session) { mutableStateOf(session?.displayName ?: "") }
    var isEditingBio by remember { mutableStateOf(false) }
    var editedBio by remember(session) { mutableStateOf(session?.bio ?: "") }
    var showLogoutDialog by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier.testTag("profile_back_button")
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Large Avatar with Change Photo option
            Box(
                modifier = Modifier.size(108.dp),
                contentAlignment = Alignment.Center
            ) {
                ContactAvatar(
                    name = session?.displayName ?: "User",
                    avatarResName = session?.avatarResName ?: "avatar_alex",
                    size = 100.dp
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(TealPrimary)
                        .clickable { viewModel.showToast("Avatar updated to next preset") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change photo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Display Name Row (Editable)
            if (isEditingName) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("edit_profile_name_input"),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            viewModel.updateProfile(editedName, session?.bio ?: "", "")
                            isEditingName = false
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Save", tint = TealPrimary)
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isEditingName = true }
                ) {
                    Text(
                        text = session?.displayName ?: "User",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Name",
                        tint = SlateMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Phone and Country
            Text(
                text = session?.phoneNumber ?: "",
                color = TealPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Bio / Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isEditingBio = true },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "About & Status",
                        fontSize = 12.sp,
                        color = SlateMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (isEditingBio) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = editedBio,
                                onValueChange = { editedBio = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            IconButton(
                                onClick = {
                                    viewModel.updateProfile(session?.displayName ?: "", editedBio, "")
                                    isEditingBio = false
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Save", tint = TealPrimary)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = session?.bio ?: "Available",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Status",
                                tint = SlateMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${allMessages.size}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                        Text(text = "Messages", fontSize = 12.sp, color = SlateMuted)
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${allCalls.size}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                        Text(text = "Calls", fontSize = 12.sp, color = SlateMuted)
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = session?.countryName?.take(6) ?: "Global",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                        Text(text = "Country", fontSize = 12.sp, color = SlateMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Shortcuts: Block List & Settings
            ProfileOptionRow(
                icon = Icons.Default.Block,
                title = "Blocked Contacts",
                subtitle = "Manage restricted numbers",
                onClick = { viewModel.navigateTo(Screen.BLOCK_LIST) },
                tag = "profile_block_list_shortcut"
            )

            Spacer(modifier = Modifier.height(10.dp))

            ProfileOptionRow(
                icon = Icons.Default.Settings,
                title = "App Settings",
                subtitle = "Ringtone, notifications, privacy",
                onClick = { viewModel.navigateTo(Screen.SETTINGS) },
                tag = "profile_settings_shortcut"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("profile_logout_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x22EF4444)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Log Out",
                    tint = CallDeclineRed
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out", color = CallDeclineRed, fontWeight = FontWeight.Bold)
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Log Out from BSL CALLING?") },
                text = { Text("You will need to verify your phone number with OTP again to log in.") },
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
fun ProfileOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(tag),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TealPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(text = subtitle, fontSize = 12.sp, color = SlateMuted)
            }
        }
    }
}
