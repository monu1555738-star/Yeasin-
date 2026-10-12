package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StoryEntity
import com.example.ui.common.ContactAvatar
import com.example.ui.story.CreateStoryDialog
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.ChannelItem
import com.example.viewmodel.MainViewModel

@Composable
fun UpdatesTab(
    viewModel: MainViewModel,
    onViewStory: (StoryEntity) -> Unit
) {
    val ownStories by viewModel.ownStories.collectAsState()
    val otherStories by viewModel.othersStories.collectAsState()
    val session by viewModel.session.collectAsState()
    val channels by viewModel.channels.collectAsState()
    var showCreateStoryDialog by remember { mutableStateOf(false) }

    // Clean white surface background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Top "Updates" Title Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Updates",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { showCreateStoryDialog = true },
                            modifier = Modifier.testTag("updates_camera_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = { viewModel.showToast("Search status & channels") },
                            modifier = Modifier.testTag("updates_search_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = { viewModel.showToast("Status privacy settings") },
                            modifier = Modifier.testTag("updates_more_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // ================================================================
            // SECTION 1: STATUS SECTION (Horizontal Circular Status Cards)
            // ================================================================
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // "Add status" Card
                        item {
                            AddStatusCard(
                                userName = session?.displayName ?: "My status",
                                avatarResName = session?.avatarResName ?: "avatar_alex",
                                hasExistingStatus = ownStories.isNotEmpty(),
                                onClick = {
                                    if (ownStories.isNotEmpty()) {
                                        onViewStory(ownStories.first())
                                    } else {
                                        showCreateStoryDialog = true
                                    }
                                },
                                onAddClick = { showCreateStoryDialog = true }
                            )
                        }

                        // Contacts' Status Cards
                        items(otherStories, key = { it.id }) { story ->
                            ContactStatusCard(
                                story = story,
                                onClick = { onViewStory(story) }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                // Divider line between Status and Channels
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(horizontal = 20.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                )
                Spacer(modifier = Modifier.height(18.dp))
            }

            // ================================================================
            // SECTION 2: CHANNELS SECTION
            // ================================================================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Channels",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Stay updated on topics you care about",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateMuted
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.showToast("Channel directory: Explore 100+ public channels") },
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("explore_channels_btn")
                        ) {
                            Text(
                                text = "Explore",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TealPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // Followed / Active Channels List
            items(channels, key = { it.id }) { channel ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                    ChannelRowCard(
                        channel = channel,
                        onToggleFollow = { viewModel.toggleFollowChannel(channel.id) },
                        onClick = { viewModel.showToast("Opened channel: ${channel.name}") }
                    )
                }
            }

            // Find Channels Discovery Banner at bottom
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Find channels to follow",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Follow verified organizations, sports teams, news desks, and creators to receive real-time updates.",
                            fontSize = 13.sp,
                            color = SlateMuted,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.showToast("Explore all verified channels") },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Discover More Channels", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Floating Action Buttons (Pencil / Text Story)
        FloatingActionButton(
            onClick = { showCreateStoryDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("updates_create_story_fab"),
            containerColor = TealPrimary,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Add Text Status")
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

// ============================================================================
// STATUS CARDS
// ============================================================================

@Composable
fun AddStatusCard(
    userName: String,
    avatarResName: String,
    hasExistingStatus: Boolean,
    onClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("add_status_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 6.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                ContactAvatar(
                    name = userName,
                    avatarResName = avatarResName,
                    size = 56.dp,
                    hasStoryRing = hasExistingStatus
                )

                // Emerald Add Badge
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(TealPrimary)
                        .clickable(onClick = onAddClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Status",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (hasExistingStatus) "My status" else "Add status",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = if (hasExistingStatus) "View" else "Tap to add",
                fontSize = 10.sp,
                color = SlateMuted
            )
        }
    }
}

@Composable
fun ContactStatusCard(
    story: StoryEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("status_card_${story.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 6.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ContactAvatar(
                name = story.authorName,
                avatarResName = story.authorAvatarResName,
                size = 56.dp,
                hasStoryRing = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = story.authorName.split(" ").firstOrNull() ?: story.authorName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Recent",
                fontSize = 10.sp,
                color = TealPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ============================================================================
// CHANNELS ROW CARD
// ============================================================================

@Composable
fun ChannelRowCard(
    channel: ChannelItem,
    onToggleFollow: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("channel_item_${channel.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Channel Avatar
            val parsedBg = try {
                Color(android.graphics.Color.parseColor(channel.avatarBgHex))
            } catch (e: Exception) {
                TealPrimary
            }

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(parsedBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = channel.avatarInitials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Channel Name & Verified Badge & Timestamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = channel.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (channel.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified Channel",
                                tint = TealPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = channel.timestamp,
                        fontSize = 11.sp,
                        color = SlateMuted
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Short Preview Text (2 lines)
                Text(
                    text = channel.latestUpdate,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom row: Followers count, Unread badge, Follow/Following button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = channel.followersText,
                        fontSize = 11.sp,
                        color = SlateMuted
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Unread Count Badge
                        if (channel.unreadCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = TealPrimary
                            ) {
                                Text(
                                    text = "${channel.unreadCount}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Follow / Following toggle
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable(onClick = onToggleFollow)
                                .testTag("follow_btn_${channel.id}"),
                            shape = RoundedCornerShape(14.dp),
                            color = if (channel.isFollowing) MaterialTheme.colorScheme.surfaceVariant else TealPrimary.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (channel.isFollowing) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = TealPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                }
                                Text(
                                    text = if (channel.isFollowing) "Following" else "+ Follow",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TealPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
