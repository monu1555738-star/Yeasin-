package com.example.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.TealPrimary

@Composable
fun ContactAvatar(
    name: String,
    avatarResName: String = "",
    avatarUri: String = "",
    size: Dp = 48.dp,
    showOnlineBadge: Boolean = false,
    isOnline: Boolean = false,
    hasStoryRing: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ringPadding = if (hasStoryRing) 3.dp else 0.dp

    Box(
        modifier = modifier
            .size(if (hasStoryRing) size + 6.dp else size),
        contentAlignment = Alignment.Center
    ) {
        val outerModifier = if (hasStoryRing) {
            Modifier
                .size(size + 6.dp)
                .border(
                    width = 2.5.dp,
                    brush = Brush.linearGradient(listOf(TealPrimary, CyanSecondary)),
                    shape = CircleShape
                )
        } else {
            Modifier
        }

        Box(
            modifier = outerModifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            when {
                avatarUri.isNotBlank() -> {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(avatarUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                avatarResName == "avatar_alex" -> {
                    Image(
                        painter = painterResource(id = R.drawable.avatar_alex),
                        contentDescription = name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                avatarResName == "avatar_sophia" -> {
                    Image(
                        painter = painterResource(id = R.drawable.avatar_sophia),
                        contentDescription = name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    // Fallback stylish initials with gradient
                    val initial = name.firstOrNull()?.uppercase() ?: "B"
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initial,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = (size.value * 0.42f).sp
                        )
                    }
                }
            }
        }

        if (showOnlineBadge && isOnline) {
            Box(
                modifier = Modifier
                    .size((size * 0.28f).coerceAtLeast(10.dp))
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color(0xFF22C55E))
                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    }
}
