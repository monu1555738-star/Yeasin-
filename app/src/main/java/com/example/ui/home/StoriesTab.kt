package com.example.ui.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.model.StoryEntity
import com.example.ui.common.ContactAvatar
import com.example.ui.story.CreateStoryDialog
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StoriesTab(
    viewModel: MainViewModel,
    onViewStory: (StoryEntity) -> Unit
) {
    val ownStories by viewModel.ownStories.collectAsState()
    val otherStories by viewModel.othersStories.collectAsState()
    val session by viewModel.session.collectAsState()
    var showCreateStoryDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Status & Stories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Photos, videos and text updates disappear after 24 hours.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateMuted
                )
                Spacer(modifier = Modifier.height(14.dp))

                // "My Status" Row
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (ownStories.isNotEmpty()) {
                                onViewStory(ownStories.first())
                            } else {
                                showCreateStoryDialog = true
                            }
                        }
                        .testTag("my_status_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            ContactAvatar(
                                name = session?.displayName ?: "Me",
                                avatarResName = session?.avatarResName ?: "avatar_alex",
                                size = 56.dp,
                                hasStoryRing = ownStories.isNotEmpty()
                            )
                            if (ownStories.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(TealPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Story",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "My Status",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (ownStories.isNotEmpty()) {
                                val latest = ownStories.first()
                                val hoursLeft = ((latest.timestamp + 86400000 - System.currentTimeMillis()) / 3600000).coerceAtLeast(1)
                                Text(
                                    text = "Expires in ${hoursLeft}h • ${latest.viewsCount} views",
                                    fontSize = 12.sp,
                                    color = TealPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Text(
                                    text = "Tap to share a story update",
                                    fontSize = 12.sp,
                                    color = SlateMuted
                                )
                            }
                        }

                        if (ownStories.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.deleteStory(ownStories.first().id) },
                                modifier = Modifier.testTag("delete_own_story_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Story",
                                    tint = SlateMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Recent Updates (${otherStories.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (otherStories.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No updates from your contacts right now.",
                            color = SlateMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(otherStories, key = { it.id }) { story ->
                    StoryContactItem(
                        story = story,
                        onClick = { onViewStory(story) }
                    )
                }
            }
        }

        // Floating Action Buttons for adding status
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            FloatingActionButton(
                onClick = { showCreateStoryDialog = true },
                containerColor = TealPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("create_story_fab")
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Add Text Story")
            }
        }

        if (showCreateStoryDialog) {
            CreateStoryDialog(
                onDismiss = { showCreateStoryDialog = false },
                onPost = { text, bgColor ->
                    viewModel.postStory(text = text, bgColor = bgColor)
                    showCreateStoryDialog = false
                }
            )
        }
    }
}

@Composable
fun StoryContactItem(
    story: StoryEntity,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("story_item_${story.id}"),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(
                name = story.authorName,
                avatarResName = story.authorAvatarResName,
                size = 52.dp,
                hasStoryRing = true
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = story.authorName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                val timeStr = formatStoryTime(story.timestamp)
                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

private fun formatStoryTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 3600000 -> "${diff / 60000} minutes ago"
        diff < 86400000 -> "${diff / 3600000} hours ago"
        else -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
    }
}
