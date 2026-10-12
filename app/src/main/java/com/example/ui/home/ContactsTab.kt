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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.model.ContactEntity
import com.example.ui.common.ContactAvatar
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.CallDeclineRed
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.MainViewModel

@Composable
fun ContactsTab(viewModel: MainViewModel) {
    val contacts by viewModel.allContacts.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddContactDialog by remember { mutableStateOf(false) }

    val filteredContacts = remember(contacts, searchQuery) {
        if (searchQuery.isBlank()) {
            contacts
        } else {
            contacts.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.phone.contains(searchQuery)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("contacts_search_input"),
                placeholder = { Text("Search ${contacts.size} contacts...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = SlateMuted)
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredContacts, key = { it.id }) { contact ->
                    ContactListItem(
                        contact = contact,
                        onChat = { viewModel.openChat(contact.id) },
                        onAudioCall = { viewModel.startCall(contact, "AUDIO") },
                        onVideoCall = { viewModel.startCall(contact, "VIDEO") },
                        onToggleBlock = { viewModel.toggleBlockContact(contact.id, contact.isBlocked) }
                    )
                }
            }
        }

        // Add Contact FAB
        FloatingActionButton(
            onClick = { showAddContactDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_contact_fab"),
            containerColor = TealPrimary,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add Contact")
        }

        if (showAddContactDialog) {
            AddContactDialog(
                onDismiss = { showAddContactDialog = false },
                onAdd = { name, phone, code ->
                    viewModel.addNewContact(name, phone, code, "Available on BSL CALLING")
                    showAddContactDialog = false
                }
            )
        }
    }
}

@Composable
fun ContactListItem(
    contact: ContactEntity,
    onChat: () -> Unit,
    onAudioCall: () -> Unit,
    onVideoCall: () -> Unit,
    onToggleBlock: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onChat)
            .testTag("contact_item_${contact.id}"),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(
                name = contact.name,
                avatarResName = contact.avatarResName,
                size = 50.dp,
                showOnlineBadge = true,
                isOnline = contact.isOnline
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (contact.isBlocked) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(Blocked)",
                            fontSize = 11.sp,
                            color = CallDeclineRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${contact.countryCode} ${contact.phone} • ${contact.statusMessage}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateMuted,
                    maxLines = 1
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                IconButton(
                    onClick = onChat,
                    modifier = Modifier.testTag("chat_contact_btn_${contact.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Chat",
                        tint = TealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onAudioCall,
                    enabled = !contact.isBlocked,
                    modifier = Modifier.testTag("audio_call_contact_btn_${contact.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Audio Call",
                        tint = if (contact.isBlocked) SlateMuted else CallAcceptGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onVideoCall,
                    enabled = !contact.isBlocked,
                    modifier = Modifier.testTag("video_call_contact_btn_${contact.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video Call",
                        tint = if (contact.isBlocked) SlateMuted else CyanSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu", tint = SlateMuted)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (contact.isBlocked) "Unblock Contact" else "Block Contact") },
                            onClick = {
                                showMenu = false
                                onToggleBlock()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = null,
                                    tint = if (contact.isBlocked) TealPrimary else CallDeclineRed
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddContactDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, phone: String, code: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("+880") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Contact", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Contact Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_contact_name_input"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Country Code (e.g. +880, +91, +1)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_contact_phone_input"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank() && phone.isNotBlank()) onAdd(name, phone, code) },
                enabled = name.isNotBlank() && phone.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Save Contact")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
