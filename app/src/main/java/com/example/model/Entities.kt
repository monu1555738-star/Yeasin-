package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_session")
data class UserSessionEntity(
    @PrimaryKey val id: Int = 1,
    val phoneNumber: String,
    val countryCode: String,
    val countryName: String,
    val displayName: String,
    val avatarUri: String = "",
    val avatarResName: String = "avatar_alex",
    val bio: String = "Hey there! I am using BSL CALLING.",
    val isLoggedIn: Boolean = false,
    val joinedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val countryCode: String,
    val avatarResName: String = "",
    val statusMessage: String = "Available",
    val isOnline: Boolean = true,
    val lastSeenText: String = "Online",
    val isBlocked: Boolean = false,
    val isFavorite: Boolean = false
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactId: Long,
    val senderPhone: String,
    val text: String,
    val mediaUri: String = "",
    val mediaType: String = "TEXT", // "TEXT", "IMAGE", "VIDEO", "AUDIO"
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "READ",     // "SENT", "DELIVERED", "READ"
    val isOutgoing: Boolean = true
)

@Entity(tableName = "call_records")
data class CallRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactId: Long = 0,
    val contactName: String,
    val contactPhone: String,
    val avatarResName: String = "",
    val callType: String = "AUDIO",      // "AUDIO", "VIDEO"
    val direction: String = "OUTGOING",  // "INCOMING", "OUTGOING", "MISSED", "REJECTED"
    val durationSeconds: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorName: String,
    val authorPhone: String,
    val authorAvatarResName: String = "",
    val contentText: String = "",
    val mediaUri: String = "",
    val mediaType: String = "TEXT",      // "TEXT", "IMAGE", "VIDEO"
    val bgColorHex: String = "#0D9488",  // Teal background for text stories
    val timestamp: Long = System.currentTimeMillis(),
    val viewsCount: Int = 0,
    val viewerNames: String = "",        // comma-separated list of viewers
    val isOwnStory: Boolean = false
)

@Entity(tableName = "app_settings")
data class SettingEntity(
    @PrimaryKey val id: Int = 1,
    val ringtoneEnabled: Boolean = true,
    val ringtoneTitle: String = "Oceanic Pulse (Default)",
    val vibrateEnabled: Boolean = true,
    val readReceiptsEnabled: Boolean = true,
    val notifyCalls: Boolean = true,
    val notifyMessages: Boolean = true
)

@Entity(tableName = "chat_groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val avatarResName: String = "avatar_alex",
    val avatarUri: String = "",
    val createdByPhone: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "group_members")
data class GroupMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val contactId: Long = 0,
    val phone: String,
    val name: String,
    val avatarResName: String = "avatar_alex",
    val role: String = "MEMBER", // "ADMIN", "MODERATOR", "MEMBER"
    val joinedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "group_messages")
data class GroupMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val senderPhone: String,
    val senderName: String,
    val senderAvatarResName: String = "avatar_alex",
    val text: String,
    val mediaUri: String = "",
    val mediaType: String = "TEXT", // "TEXT", "IMAGE", "AUDIO", "SYSTEM"
    val timestamp: Long = System.currentTimeMillis(),
    val isOutgoing: Boolean = false
)
