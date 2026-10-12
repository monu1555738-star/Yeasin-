package com.example.ui.chat

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.ChatMessageEntity
import com.example.ui.common.ContactAvatar
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.CallDeclineRed
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateChatScreen(viewModel: MainViewModel) {
    val contactId by viewModel.activeChatContactId.collectAsState()
    val allContacts by viewModel.allContacts.collectAsState()
    val messages by viewModel.chatMessages.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val contact = allContacts.find { it.id == contactId }

    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Modern Android Photo Picker (zero-permission)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            viewModel.sendMessage(
                text = "Sent a photo",
                mediaUri = it.toString(),
                mediaType = "IMAGE"
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { /* View profile */ }
                    ) {
                        ContactAvatar(
                            name = contact?.name ?: "Contact",
                            avatarResName = contact?.avatarResName ?: "",
                            size = 38.dp,
                            showOnlineBadge = true,
                            isOnline = contact?.isOnline ?: false
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = contact?.name ?: "Chat",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (contact?.isBlocked == true) "Blocked" else (contact?.lastSeenText ?: "Online"),
                                fontSize = 11.sp,
                                color = if (contact?.isBlocked == true) CallDeclineRed else TealPrimary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier.testTag("chat_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { contact?.let { viewModel.startCall(it, "AUDIO") } },
                        enabled = contact?.isBlocked != true,
                        modifier = Modifier.testTag("chat_audio_call_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Audio Call",
                            tint = if (contact?.isBlocked == true) SlateMuted else CallAcceptGreen
                        )
                    }

                    IconButton(
                        onClick = { contact?.let { viewModel.startCall(it, "VIDEO") } },
                        enabled = contact?.isBlocked != true,
                        modifier = Modifier.testTag("chat_video_call_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call",
                            tint = if (contact?.isBlocked == true) SlateMuted else CyanSecondary
                        )
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (contact?.isBlocked == true) "Unblock Contact" else "Block Contact") },
                                onClick = {
                                    showMenu = false
                                    contact?.let { viewModel.toggleBlockContact(it.id, it.isBlocked) }
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = null,
                                        tint = if (contact?.isBlocked == true) TealPrimary else CallDeclineRed
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Clear Chat") },
                                onClick = {
                                    showMenu = false
                                    viewModel.clearActiveChat()
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (contact?.isBlocked == true) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "You blocked this contact.",
                            color = CallDeclineRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        TextButton(onClick = { viewModel.toggleBlockContact(contact.id, true) }) {
                            Text("Unblock", fontWeight = FontWeight.Bold, color = TealPrimary)
                        }
                    }
                }
            } else {
                ChatInputBar(
                    text = inputText,
                    onTextChange = { inputText = it },
                    onSend = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    onPickPhoto = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onCameraCapture = {
                        // Quick camera shot simulation
                        viewModel.sendMessage(
                            text = "📷 Direct Camera Photo",
                            mediaUri = "camera_sample",
                            mediaType = "IMAGE"
                        )
                    },
                    onSendAudioMemo = {
                        viewModel.sendMessage(
                            text = "🎤 Voice note (0:07)",
                            mediaType = "AUDIO"
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageBubble(message = msg)
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessageEntity) {
    val isOutgoing = message.isOutgoing
    val alignment = if (isOutgoing) Alignment.End else Alignment.Start
    val bgColor = if (isOutgoing) TealPrimary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isOutgoing) Color.White else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isOutgoing) 16.dp else 2.dp,
                bottomEnd = if (isOutgoing) 2.dp else 16.dp
            ),
            color = bgColor,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                // Media preview if image
                if (message.mediaType == "IMAGE") {
                    if (message.mediaUri.startsWith("content://") || message.mediaUri.startsWith("http")) {
                        AsyncImage(
                            model = message.mediaUri,
                            contentDescription = "Photo Attachment",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    } else {
                        // Styled camera photo placeholder
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0x33000000))
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Photo Snapshot",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                } else if (message.mediaType == "AUDIO") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Voice message",
                            tint = if (isOutgoing) Color.White else TealPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Voice Memo 0:07",
                            color = textColor,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }

                if (message.text.isNotBlank()) {
                    Text(
                        text = message.text,
                        color = textColor,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))
                    Text(
                        text = timeStr,
                        fontSize = 10.sp,
                        color = if (isOutgoing) Color(0xCCFFFFFF) else SlateMuted
                    )

                    if (isOutgoing) {
                        Spacer(modifier = Modifier.width(4.dp))
                        when (message.status) {
                            "READ" -> Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Read",
                                tint = Color(0xFF67E8F9),
                                modifier = Modifier.size(13.dp)
                            )
                            "DELIVERED" -> Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Delivered",
                                tint = Color(0xCCFFFFFF),
                                modifier = Modifier.size(13.dp)
                            )
                            else -> Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Sent",
                                tint = Color(0xCCFFFFFF),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onPickPhoto: () -> Unit,
    onCameraCapture: () -> Unit,
    onSendAudioMemo: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPickPhoto,
                modifier = Modifier.testTag("chat_attach_photo_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Attach Photo",
                    tint = TealPrimary
                )
            }

            IconButton(
                onClick = onCameraCapture,
                modifier = Modifier.testTag("chat_camera_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Take Photo",
                    tint = TealPrimary
                )
            }

            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                placeholder = { Text("Message...", fontSize = 14.sp) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                maxLines = 4,
                singleLine = false,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() })
            )

            Spacer(modifier = Modifier.width(6.dp))

            if (text.isNotBlank()) {
                IconButton(
                    onClick = onSend,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(TealPrimary)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = onSendAudioMemo,
                    modifier = Modifier.testTag("chat_mic_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Record Voice",
                        tint = TealPrimary
                    )
                }
            }
        }
    }
}
