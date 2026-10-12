package com.example.data

import com.example.model.CallRecordEntity
import com.example.model.ChatMessageEntity
import com.example.model.ContactEntity
import com.example.model.SettingEntity
import com.example.model.StoryEntity
import com.example.model.UserSessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AppRepository(private val db: AppDatabase) {
    private val sessionDao = db.userSessionDao()
    private val contactDao = db.contactDao()
    private val chatDao = db.chatDao()
    private val callDao = db.callDao()
    private val storyDao = db.storyDao()
    private val settingDao = db.settingDao()

    val sessionFlow: Flow<UserSessionEntity?> = sessionDao.getSession()
    val activeContacts: Flow<List<ContactEntity>> = contactDao.getActiveContacts()
    val allContacts: Flow<List<ContactEntity>> = contactDao.getAllContacts()
    val blockedContacts: Flow<List<ContactEntity>> = contactDao.getBlockedContacts()
    val allCalls: Flow<List<CallRecordEntity>> = callDao.getAllCalls()
    val missedCalls: Flow<List<CallRecordEntity>> = callDao.getMissedCalls()
    val allMessages: Flow<List<ChatMessageEntity>> = chatDao.getAllMessages()
    val settingsFlow: Flow<SettingEntity?> = settingDao.getSettings()

    fun getMessagesForContact(contactId: Long): Flow<List<ChatMessageEntity>> =
        chatDao.getMessagesForContact(contactId)

    fun getContactById(id: Long): Flow<ContactEntity?> = contactDao.getContactById(id)

    suspend fun getContactByIdSync(id: Long): ContactEntity? = contactDao.getContactByIdSync(id)

    fun getActiveStories(): Flow<List<StoryEntity>> {
        val cutoff = System.currentTimeMillis() - 24 * 60 * 60 * 1000
        return storyDao.getActiveStories(cutoff)
    }

    fun getOwnStories(): Flow<List<StoryEntity>> {
        val cutoff = System.currentTimeMillis() - 24 * 60 * 60 * 1000
        return storyDao.getOwnStories(cutoff)
    }

    fun getOthersStories(): Flow<List<StoryEntity>> {
        val cutoff = System.currentTimeMillis() - 24 * 60 * 60 * 1000
        return storyDao.getOthersStories(cutoff)
    }

    suspend fun loginUser(
        phoneNumber: String,
        countryCode: String,
        countryName: String,
        displayName: String,
        bio: String = "Available"
    ) {
        val session = UserSessionEntity(
            id = 1,
            phoneNumber = phoneNumber,
            countryCode = countryCode,
            countryName = countryName,
            displayName = displayName.ifBlank { "User ${phoneNumber.takeLast(4)}" },
            bio = bio,
            isLoggedIn = true,
            avatarResName = "avatar_alex"
        )
        sessionDao.insertSession(session)
    }

    suspend fun logout() {
        sessionDao.logout()
    }

    suspend fun updateProfile(displayName: String, bio: String, avatarUri: String) {
        sessionDao.updateProfile(displayName, bio, avatarUri)
    }

    suspend fun sendMessage(
        contactId: Long,
        senderPhone: String,
        text: String,
        mediaUri: String = "",
        mediaType: String = "TEXT",
        isOutgoing: Boolean = true
    ): Long {
        val message = ChatMessageEntity(
            contactId = contactId,
            senderPhone = senderPhone,
            text = text,
            mediaUri = mediaUri,
            mediaType = mediaType,
            timestamp = System.currentTimeMillis(),
            status = if (isOutgoing) "READ" else "DELIVERED",
            isOutgoing = isOutgoing
        )
        return chatDao.insertMessage(message)
    }

    suspend fun clearChat(contactId: Long) {
        chatDao.clearChatForContact(contactId)
    }

    suspend fun recordCall(
        contactId: Long,
        contactName: String,
        contactPhone: String,
        avatarResName: String,
        callType: String,
        direction: String,
        durationSeconds: Int
    ): Long {
        val record = CallRecordEntity(
            contactId = contactId,
            contactName = contactName,
            contactPhone = contactPhone,
            avatarResName = avatarResName,
            callType = callType,
            direction = direction,
            durationSeconds = durationSeconds,
            timestamp = System.currentTimeMillis()
        )
        return callDao.insertCall(record)
    }

    suspend fun deleteCall(id: Long) {
        callDao.deleteCall(id)
    }

    suspend fun clearAllCalls() {
        callDao.clearAllCalls()
    }

    suspend fun blockContact(id: Long, isBlocked: Boolean) {
        contactDao.setBlocked(id, isBlocked)
    }

    suspend fun addContact(
        name: String,
        phone: String,
        countryCode: String,
        statusMessage: String = "Available"
    ): Long {
        val contact = ContactEntity(
            name = name,
            phone = phone,
            countryCode = countryCode,
            avatarResName = "avatar_alex",
            statusMessage = statusMessage,
            isOnline = true
        )
        return contactDao.insertContact(contact)
    }

    suspend fun postStory(
        authorName: String,
        authorPhone: String,
        contentText: String,
        mediaUri: String = "",
        mediaType: String = "TEXT",
        bgColorHex: String = "#0D9488"
    ): Long {
        val story = StoryEntity(
            authorName = authorName,
            authorPhone = authorPhone,
            authorAvatarResName = "avatar_alex",
            contentText = contentText,
            mediaUri = mediaUri,
            mediaType = mediaType,
            bgColorHex = bgColorHex,
            timestamp = System.currentTimeMillis(),
            viewsCount = 0,
            viewerNames = "",
            isOwnStory = true
        )
        return storyDao.insertStory(story)
    }

    suspend fun deleteStory(id: Long) {
        storyDao.deleteStory(id)
    }

    suspend fun recordStoryView(id: Long, viewerName: String) {
        storyDao.recordStoryView(id, viewerName)
    }

    suspend fun updateRingtoneEnabled(enabled: Boolean) {
        settingDao.setRingtoneEnabled(enabled)
    }

    suspend fun updateRingtoneTitle(title: String) {
        settingDao.setRingtoneTitle(title)
    }

    suspend fun updateVibrateEnabled(enabled: Boolean) {
        settingDao.setVibrateEnabled(enabled)
    }

    suspend fun updateReadReceipts(enabled: Boolean) {
        settingDao.setReadReceiptsEnabled(enabled)
    }

    suspend fun seedInitialData() {
        // Initialize settings if absent
        val existingSettings = settingDao.getSettings().firstOrNull()
        if (existingSettings == null) {
            settingDao.insertSettings(SettingEntity())
        }

        // Seed contacts if empty
        val count = contactDao.getContactCount()
        if (count == 0) {
            val contactsList = listOf(
                ContactEntity(
                    id = 1,
                    name = "Sophia Miller",
                    phone = "1712-345678",
                    countryCode = "+880",
                    avatarResName = "avatar_sophia",
                    statusMessage = "Enjoying the sea breeze 🌊",
                    isOnline = true,
                    lastSeenText = "Online",
                    isBlocked = false
                ),
                ContactEntity(
                    id = 2,
                    name = "Alex Rodriguez",
                    phone = "98765-43210",
                    countryCode = "+91",
                    avatarResName = "avatar_alex",
                    statusMessage = "Working on BSL CALLING features 🚀",
                    isOnline = true,
                    lastSeenText = "Online",
                    isBlocked = false
                ),
                ContactEntity(
                    id = 3,
                    name = "Tariq Al-Mansoor",
                    phone = "50-1234567",
                    countryCode = "+971",
                    avatarResName = "avatar_alex",
                    statusMessage = "In a meeting in Dubai 🏙️",
                    isOnline = false,
                    lastSeenText = "Last seen 15m ago",
                    isBlocked = false
                ),
                ContactEntity(
                    id = 4,
                    name = "Dr. Fatima Al-Zahra",
                    phone = "50-9876543",
                    countryCode = "+966",
                    avatarResName = "avatar_sophia",
                    statusMessage = "Available for calls",
                    isOnline = true,
                    lastSeenText = "Online",
                    isBlocked = false
                ),
                ContactEntity(
                    id = 5,
                    name = "James Wilson",
                    phone = "7911-123456",
                    countryCode = "+44",
                    avatarResName = "avatar_alex",
                    statusMessage = "London calling 🇬🇧",
                    isOnline = false,
                    lastSeenText = "Last seen yesterday",
                    isBlocked = false
                ),
                ContactEntity(
                    id = 6,
                    name = "Emma Watson",
                    phone = "202-555-0143",
                    countryCode = "+1",
                    avatarResName = "avatar_sophia",
                    statusMessage = "Urgent calls only please",
                    isOnline = false,
                    lastSeenText = "Last seen 2h ago",
                    isBlocked = false
                )
            )
            contactDao.insertContacts(contactsList)

            // Seed initial chat messages for Sophia Miller
            val now = System.currentTimeMillis()
            chatDao.insertMessage(
                ChatMessageEntity(
                    contactId = 1,
                    senderPhone = "+8801712345678",
                    text = "Hey! Welcome to BSL CALLING! How is the audio quality?",
                    timestamp = now - 3600000,
                    status = "READ",
                    isOutgoing = false
                )
            )
            chatDao.insertMessage(
                ChatMessageEntity(
                    contactId = 1,
                    senderPhone = "me",
                    text = "Crystal clear! The voice and video calling work seamlessly 📞✨",
                    timestamp = now - 3500000,
                    status = "READ",
                    isOutgoing = true
                )
            )
            chatDao.insertMessage(
                ChatMessageEntity(
                    contactId = 1,
                    senderPhone = "+8801712345678",
                    text = "Awesome! Let me know when you want to try the video call test.",
                    timestamp = now - 1800000,
                    status = "READ",
                    isOutgoing = false
                )
            )

            // Seed initial chat for Alex
            chatDao.insertMessage(
                ChatMessageEntity(
                    contactId = 2,
                    senderPhone = "+919876543210",
                    text = "Hi there, I tested the country code selector and phone OTP, it works like a charm!",
                    timestamp = now - 7200000,
                    status = "READ",
                    isOutgoing = false
                )
            )

            // Seed call history
            callDao.insertCall(
                CallRecordEntity(
                    contactId = 1,
                    contactName = "Sophia Miller",
                    contactPhone = "+880 1712-345678",
                    avatarResName = "avatar_sophia",
                    callType = "AUDIO",
                    direction = "INCOMING",
                    durationSeconds = 142,
                    timestamp = now - 1800000
                )
            )
            callDao.insertCall(
                CallRecordEntity(
                    contactId = 2,
                    contactName = "Alex Rodriguez",
                    contactPhone = "+91 98765-43210",
                    avatarResName = "avatar_alex",
                    callType = "VIDEO",
                    direction = "OUTGOING",
                    durationSeconds = 315,
                    timestamp = now - 14400000
                )
            )
            callDao.insertCall(
                CallRecordEntity(
                    contactId = 3,
                    contactName = "Tariq Al-Mansoor",
                    contactPhone = "+971 50-1234567",
                    avatarResName = "avatar_alex",
                    callType = "AUDIO",
                    direction = "MISSED",
                    durationSeconds = 0,
                    timestamp = now - 28800000
                )
            )

            // Seed stories
            storyDao.insertStory(
                StoryEntity(
                    authorName = "Sophia Miller",
                    authorPhone = "+880 1712-345678",
                    authorAvatarResName = "avatar_sophia",
                    contentText = "Sunset by the bay 🌅 Hope everyone is having a peaceful evening!",
                    bgColorHex = "#0284C7",
                    timestamp = now - 10800000,
                    viewsCount = 14,
                    viewerNames = "Alex, Tariq, Emma",
                    isOwnStory = false
                )
            )
            storyDao.insertStory(
                StoryEntity(
                    authorName = "Alex Rodriguez",
                    authorPhone = "+91 98765-43210",
                    authorAvatarResName = "avatar_alex",
                    contentText = "Testing the brand new BSL CALLING high definition video pipeline! 🚀📱",
                    bgColorHex = "#0D9488",
                    timestamp = now - 7200000,
                    viewsCount = 22,
                    viewerNames = "Sophia, Fatima, James",
                    isOwnStory = false
                )
            )
        }
    }
}
