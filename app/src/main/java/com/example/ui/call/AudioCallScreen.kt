package com.example.ui.call

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.common.ContactAvatar
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.CallDeclineRed
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.SlateLight
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.CallStatus
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioCallScreen(viewModel: MainViewModel) {
    val activeCall by viewModel.activeCall.collectAsState()
    var showDialpad by remember { mutableStateOf(false) }
    val dialpadState = rememberModalBottomSheetState()

    BackHandler {
        // Prevent accidental back exit without ending call
        viewModel.endCall()
    }

    val call = activeCall ?: return
    val contact = call.contact

    // Pulsing animation for calling/ringing
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkNavyBackground,
                        Color(0xFF0F1E36),
                        DarkNavyBackground
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: App Branding & Status
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 28.dp)
            ) {
                Text(
                    text = "BSL CALLING HD Voice",
                    color = TealPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = contact.name,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${contact.countryCode} ${contact.phone}",
                    color = SlateLight,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Call Status / Real-time Timer
                when (call.status) {
                    CallStatus.INCOMING -> {
                        Text(
                            text = "Incoming Audio Call...",
                            color = CallAcceptGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    CallStatus.OUTGOING_RINGING -> {
                        Text(
                            text = "Ringing...",
                            color = CyanSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    CallStatus.CONNECTED -> {
                        val minutes = call.durationSeconds / 60
                        val seconds = call.durationSeconds % 60
                        val timerStr = String.format("%02d:%02d", minutes, seconds)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CallAcceptGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = timerStr,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    else -> {}
                }
            }

            // Middle Section: Pulsing Avatar with Wave Effect
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(if (call.status == CallStatus.CONNECTED) 1.05f else pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    TealPrimary.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                ContactAvatar(
                    name = contact.name,
                    avatarResName = contact.avatarResName,
                    size = 120.dp
                )
            }

            // Bottom Section: Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                if (call.status == CallStatus.INCOMING) {
                    // Incoming call: Accept (green) and Decline (red)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Decline Button
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = { viewModel.declineIncomingCall() },
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(CallDeclineRed)
                                    .testTag("decline_call_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CallEnd,
                                    contentDescription = "Decline",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Decline", color = Color.White, fontSize = 13.sp)
                        }

                        // Accept Button
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = { viewModel.acceptIncomingCall() },
                                modifier = Modifier
                                    .size(68.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(CallAcceptGreen)
                                    .testTag("accept_call_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Accept",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Accept", color = Color.White, fontSize = 13.sp)
                        }
                    }
                } else {
                    // Connected or Outgoing Call Controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mute Button
                        CallActionButton(
                            icon = if (call.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            label = if (call.isMuted) "Unmute" else "Mute",
                            isActive = call.isMuted,
                            onClick = { viewModel.toggleMute() },
                            tag = "call_mute_toggle"
                        )

                        // Speaker Button
                        CallActionButton(
                            icon = if (call.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                            label = if (call.isSpeakerOn) "Speaker On" else "Speaker",
                            isActive = call.isSpeakerOn,
                            onClick = { viewModel.toggleSpeaker() },
                            tag = "call_speaker_toggle"
                        )

                        // Dialpad Button
                        CallActionButton(
                            icon = Icons.Default.Dialpad,
                            label = "Keypad",
                            isActive = false,
                            onClick = { showDialpad = true },
                            tag = "call_dialpad_button"
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // End Call Button
                    IconButton(
                        onClick = { viewModel.endCall() },
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(CallDeclineRed)
                            .testTag("end_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }
        }

        // Dialpad sheet
        if (showDialpad) {
            ModalBottomSheet(
                onDismissRequest = { showDialpad = false },
                sheetState = dialpadState,
                containerColor = Color(0xFF131D33)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Call Keypad",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val keys = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("*", "0", "#")
                    )

                    keys.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            row.forEach { key ->
                                Surface(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape),
                                    color = Color(0xFF1E2D4A)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = key,
                                            color = Color.White,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun CallActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (isActive) TealPrimary else Color(0x33FFFFFF))
                .testTag(tag)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = SlateLight,
            fontSize = 12.sp
        )
    }
}
