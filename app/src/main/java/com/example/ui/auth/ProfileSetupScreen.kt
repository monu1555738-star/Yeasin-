package com.example.ui.auth

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.MainViewModel

@Composable
fun ProfileSetupScreen(viewModel: MainViewModel) {
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val phoneInput by viewModel.phoneInput.collectAsState()
    val nameInput by viewModel.setupNameInput.collectAsState()
    val bioInput by viewModel.setupBioInput.collectAsState()
    var selectedAvatarRes by remember { mutableStateOf("avatar_alex") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Set Up Your Profile",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Your friends will see this name and photo when you call or message them.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Avatar Preview with Camera badge
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            ContactAvatar(
                name = nameInput.ifEmpty { "User" },
                avatarResName = selectedAvatarRes,
                size = 96.dp
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(TealPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Change Photo",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Preset Avatars Selector
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("avatar_alex", "avatar_sophia").forEach { avatarName ->
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { selectedAvatarRes = avatarName }
                        .padding(4.dp)
                ) {
                    ContactAvatar(
                        name = if (avatarName == "avatar_alex") "Alex" else "Sophia",
                        avatarResName = avatarName,
                        size = 44.dp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Verified Phone info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedCountry.flag, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Verified Phone",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${selectedCountry.dialCode} $phoneInput",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Name input
        OutlinedTextField(
            value = nameInput,
            onValueChange = { viewModel.setupNameInput.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_name_input"),
            label = { Text("Your Name (required)") },
            placeholder = { Text("e.g. Tariq Ahmed") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = null)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bio/Status input
        OutlinedTextField(
            value = bioInput,
            onValueChange = { viewModel.setupBioInput.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_bio_input"),
            label = { Text("Status / About") },
            placeholder = { Text("e.g. Available, In a call...") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.completeProfileRegistration() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("complete_profile_button"),
            enabled = nameInput.isNotBlank(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Enter BSL CALLING",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
