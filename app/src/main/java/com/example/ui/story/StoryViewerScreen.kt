package com.example.ui.story

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.common.ContactAvatar
import com.example.ui.theme.CallDeclineRed
import com.example.ui.theme.SlateLight
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryViewerScreen(viewModel: MainViewModel) {
    val story by viewModel.viewingStory.collectAsState()
    val progress = remember { Animatable(0f) }
    var showViewersSheet by remember { mutableStateOf(false) }
    val viewersSheetState = rememberModalBottomSheetState()

    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    val activeStory = story
    if (activeStory == null) {
        LaunchedEffect(Unit) {
            viewModel.navigateTo(Screen.HOME)
        }
        return
    }

    // 5-second automatic progression
    LaunchedEffect(activeStory.id) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 5000, easing = LinearEasing)
        )
        viewModel.navigateTo(Screen.HOME)
    }

    val bgColor = remember(activeStory.bgColorHex) {
        try {
            Color(android.graphics.Color.parseColor(activeStory.bgColorHex))
        } catch (e: Exception) {
            TealPrimary
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Tap regions: Left to go back, Right to advance/exit
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { viewModel.navigateTo(Screen.HOME) }
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { viewModel.navigateTo(Screen.HOME) }
            )
        }

        // Top Overlay: Progress bar & Author Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
        ) {
            LinearProgressIndicator(
                progress = { progress.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = Color.White,
                trackColor = Color(0x66FFFFFF),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ContactAvatar(
                        name = activeStory.authorName,
                        avatarResName = activeStory.authorAvatarResName,
                        size = 38.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = activeStory.authorName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(activeStory.timestamp))
                        Text(
                            text = "Today at $timeStr",
                            color = Color(0xCCFFFFFF),
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (activeStory.isOwnStory) {
                        IconButton(
                            onClick = { viewModel.deleteStory(activeStory.id) },
                            modifier = Modifier.testTag("viewer_delete_story_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Story",
                                tint = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier.testTag("viewer_close_story_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Center Story Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = activeStory.contentText,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )
        }

        // Bottom Viewers Indicator (if own story)
        if (activeStory.isOwnStory) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 28.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { showViewersSheet = true }
                    .testTag("story_viewers_button"),
                color = Color(0x66000000)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Views",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${activeStory.viewsCount} views",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Viewers Sheet
        if (showViewersSheet) {
            ModalBottomSheet(
                onDismissRequest = { showViewersSheet = false },
                sheetState = viewersSheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Story Viewers (${activeStory.viewsCount})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    val viewersList = if (activeStory.viewerNames.isNotBlank()) {
                        activeStory.viewerNames.split(", ")
                    } else {
                        listOf("Alex Rodriguez", "Sophia Miller", "Tariq Al-Mansoor")
                    }
                    viewersList.forEach { viewer ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ContactAvatar(name = viewer, size = 36.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = viewer, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
