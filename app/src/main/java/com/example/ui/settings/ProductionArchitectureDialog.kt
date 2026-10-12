package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.TealPrimary

@Composable
fun ProductionArchitectureDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "BSL CALLING Architecture",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Production backend services & protocols for BSL CALLING deployment:",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateMuted
                )

                // 1. WebRTC & Audio/Video Signaling
                ArchSectionCard(
                    icon = Icons.Default.Call,
                    title = "WebRTC & Video Calling",
                    tech = "Node.js WebSocket Signaling + coturn STUN/TURN",
                    description = "Peer-to-peer audio/video streaming via WebRTC SDP offer/answer exchange, ICE candidates, and SFU (LiveKit / Agora) for multi-party mesh calls."
                )

                // 2. Database
                ArchSectionCard(
                    icon = Icons.Default.Storage,
                    title = "Database Layer",
                    tech = "PostgreSQL 16 + Room SQLite Cache",
                    description = "PostgreSQL with connection pooling (PgBouncer) for user profiles, partitioned messages, contact rosters, and call logs. Local Room handles offline storage."
                )

                // 3. Realtime Chat
                ArchSectionCard(
                    icon = Icons.Default.Web,
                    title = "Real-time Messaging",
                    tech = "WebSockets (ws) + Redis Pub/Sub",
                    description = "Persistent bi-directional WebSocket connection for instant chat delivery, typing status, and read receipt acknowledgement."
                )

                // 4. Push Notifications
                ArchSectionCard(
                    icon = Icons.Default.Notifications,
                    title = "Push Notifications",
                    tech = "Firebase Cloud Messaging (FCM v1)",
                    description = "Dispatches high-priority VoIP alerts and message previews to wake up the Android device when app is in the background or killed."
                )

                // 5. Phone OTP & Security
                ArchSectionCard(
                    icon = Icons.Default.Lock,
                    title = "Phone Authentication",
                    tech = "Twilio Verify / Firebase Phone Auth",
                    description = "Carrier-grade SMS gateway delivering time-limited OTP tokens across 190+ countries with rate limiting and replay prevention."
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                modifier = Modifier.testTag("close_arch_dialog_btn")
            ) {
                Text("Got It")
            }
        }
    )
}

@Composable
fun ArchSectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    tech: String,
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = tech,
                        fontSize = 11.sp,
                        color = CyanSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}
