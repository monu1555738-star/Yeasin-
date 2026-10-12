package com.example.ui.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CallRecordEntity
import com.example.model.ContactEntity
import com.example.ui.common.ContactAvatar
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.CallDeclineRed
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallsTab(
    viewModel: MainViewModel,
    onStartNewCall: () -> Unit
) {
    val allCalls by viewModel.recentCalls.collectAsState()
    val activeContacts by viewModel.activeContacts.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL" or "MISSED"
    var showIncomingSimulateSheet by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val displayedCalls = remember(allCalls, selectedFilter) {
        if (selectedFilter == "MISSED") {
            allCalls.filter { it.direction == "MISSED" }
        } else {
            allCalls
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Filter chips & Clear History
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("All (${allCalls.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_all_calls")
                    )
                    FilterChip(
                        selected = selectedFilter == "MISSED",
                        onClick = { selectedFilter = "MISSED" },
                        label = {
                            val missedCount = allCalls.count { it.direction == "MISSED" }
                            Text("Missed ($missedCount)")
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CallDeclineRed,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_missed_calls")
                    )
                }

                if (allCalls.isNotEmpty()) {
                    TextButton(
                        onClick = { showClearConfirmDialog = true },
                        modifier = Modifier.testTag("clear_calls_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear",
                            tint = SlateMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear", color = SlateMuted, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (displayedCalls.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = SlateMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedFilter == "MISSED") "No missed calls" else "No call history yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Audio & video calls will appear here",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateMuted
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showIncomingSimulateSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            modifier = Modifier.testTag("simulate_call_empty_state_button")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test Incoming Call Screen")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(displayedCalls, key = { it.id }) { call ->
                        CallItemRow(
                            call = call,
                            onAudioCall = {
                                val contact = activeContacts.find { it.id == call.contactId }
                                    ?: ContactEntity(
                                        id = call.contactId,
                                        name = call.contactName,
                                        phone = call.contactPhone,
                                        countryCode = "",
                                        avatarResName = call.avatarResName
                                    )
                                viewModel.startCall(contact, "AUDIO")
                            },
                            onVideoCall = {
                                val contact = activeContacts.find { it.id == call.contactId }
                                    ?: ContactEntity(
                                        id = call.contactId,
                                        name = call.contactName,
                                        phone = call.contactPhone,
                                        countryCode = "",
                                        avatarResName = call.avatarResName
                                    )
                                viewModel.startCall(contact, "VIDEO")
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Buttons: Dialpad & Incoming Simulator
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FloatingActionButton(
                onClick = { showIncomingSimulateSheet = true },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.testTag("simulate_incoming_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Simulate Incoming Call"
                )
            }

            FloatingActionButton(
                onClick = onStartNewCall,
                containerColor = TealPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("new_call_fab")
            ) {
                Icon(imageVector = Icons.Default.Phone, contentDescription = "New Call")
            }
        }

        // Simulate Incoming Call Sheet
        if (showIncomingSimulateSheet) {
            ModalBottomSheet(
                onDismissRequest = { showIncomingSimulateSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Simulate Incoming Call",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Test incoming ringtone, ring screen, vibration, accept, and decline buttons:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    activeContacts.take(4).forEach { contact ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ContactAvatar(
                                        name = contact.name,
                                        avatarResName = contact.avatarResName,
                                        size = 40.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = contact.name,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = contact.phone,
                                            fontSize = 12.sp,
                                            color = SlateMuted
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            showIncomingSimulateSheet = false
                                            viewModel.simulateIncomingCall(contact, "AUDIO")
                                        },
                                        modifier = Modifier.testTag("sim_audio_${contact.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Call,
                                            contentDescription = null,
                                            tint = CallAcceptGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Audio", fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            showIncomingSimulateSheet = false
                                            viewModel.simulateIncomingCall(contact, "VIDEO")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyanSecondary),
                                        modifier = Modifier.testTag("sim_video_${contact.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Videocam,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Video", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Clear History Confirm Dialog
        if (showClearConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showClearConfirmDialog = false },
                title = { Text("Clear Call History?") },
                text = { Text("Are you sure you want to delete all call logs? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearCallHistory()
                            showClearConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CallDeclineRed)
                    ) {
                        Text("Clear All")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun CallItemRow(
    call: CallRecordEntity,
    onAudioCall: () -> Unit,
    onVideoCall: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .testTag("call_item_${call.id}"),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(
                name = call.contactName,
                avatarResName = call.avatarResName,
                size = 48.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = call.contactName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (call.direction == "MISSED") CallDeclineRed else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    when (call.direction) {
                        "INCOMING" -> Icon(
                            imageVector = Icons.AutoMirrored.Filled.CallReceived,
                            contentDescription = "Incoming Call",
                            tint = CallAcceptGreen,
                            modifier = Modifier.size(15.dp)
                        )
                        "OUTGOING" -> Icon(
                            imageVector = Icons.AutoMirrored.Filled.CallMade,
                            contentDescription = "Outgoing Call",
                            tint = CyanSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        "MISSED" -> Icon(
                            imageVector = Icons.AutoMirrored.Filled.CallMissed,
                            contentDescription = "Missed Call",
                            tint = CallDeclineRed,
                            modifier = Modifier.size(15.dp)
                        )
                        else -> Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Rejected Call",
                            tint = SlateMuted,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    val timeStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(call.timestamp))
                    val durationStr = if (call.durationSeconds > 0) {
                        val m = call.durationSeconds / 60
                        val s = call.durationSeconds % 60
                        " • ${m}m ${s}s"
                    } else if (call.direction == "MISSED") {
                        " • Missed"
                    } else {
                        " • Rejected"
                    }

                    Text(
                        text = "$timeStr$durationStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateMuted,
                        fontSize = 12.sp
                    )
                }
            }

            // Quick Call Actions
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onAudioCall,
                    modifier = Modifier.testTag("call_audio_btn_${call.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = TealPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = onVideoCall,
                    modifier = Modifier.testTag("call_video_btn_${call.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video Call",
                        tint = CyanSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
