package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.CallToneManager
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.model.CallRecordEntity
import com.example.model.ChatMessageEntity
import com.example.model.ContactEntity
import com.example.model.Country
import com.example.model.CountryData
import com.example.model.SettingEntity
import com.example.model.StoryEntity
import com.example.model.UserSessionEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

import kotlinx.coroutines.flow.map

enum class Screen {
    AUTH_LOGIN,
    AUTH_OTP,
    AUTH_PROFILE,
    HOME,
    CHAT_DETAIL,
    AUDIO_CALL,
    VIDEO_CALL,
    PROFILE,
    SETTINGS,
    BLOCK_LIST,
    STORY_VIEWER
}

enum class HomeTab {
    CHATS,
    UPDATES,
    CALLS
}

data class ChannelItem(
    val id: String,
    val name: String,
    val avatarInitials: String,
    val avatarBgHex: String,
    val isVerified: Boolean = true,
    val latestUpdate: String,
    val timestamp: String,
    val unreadCount: Int = 0,
    val isFollowing: Boolean = true,
    val followersText: String = "1.2M followers"
)

enum class CallStatus {
    IDLE,
    INCOMING,
    OUTGOING_RINGING,
    CONNECTED,
    ENDED
}

data class ActiveCall(
    val contact: ContactEntity,
    val callType: String, // "AUDIO" or "VIDEO"
    val status: CallStatus,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val isCameraOn: Boolean = true,
    val isFrontCamera: Boolean = true
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    val repository = AppRepository(db)
    val toneManager = CallToneManager(application)
    val userPreferences = com.example.data.UserPreferences(application)

    val userPreferencesConfig: StateFlow<com.example.data.UserPreferencesConfig> = userPreferences.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, com.example.data.UserPreferencesConfig())

    // Navigation & Screen Stack
    private val _currentScreen = MutableStateFlow(Screen.AUTH_LOGIN)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentHomeTab = MutableStateFlow(HomeTab.UPDATES)
    val currentHomeTab: StateFlow<HomeTab> = _currentHomeTab.asStateFlow()

    // Channels State
    private val _channels = MutableStateFlow(
        listOf(
            ChannelItem(
                id = "ch_tech",
                name = "Tech Pulse Official",
                avatarInitials = "TP",
                avatarBgHex = "#0D9488",
                latestUpdate = "⚡ Qualcomm & Google announce breakthrough next-gen on-device AI accelerators with 4x energy efficiency.",
                timestamp = "10:45 AM",
                unreadCount = 3,
                isFollowing = true,
                followersText = "2.4M followers"
            ),
            ChannelItem(
                id = "ch_sports",
                name = "World Sports Live",
                avatarInitials = "WS",
                avatarBgHex = "#0284C7",
                latestUpdate = "⚽ Stunning stoppage-time header seals dramatic 3-2 victory in the Champions League thriller!",
                timestamp = "9:20 AM",
                unreadCount = 5,
                isFollowing = true,
                followersText = "5.8M followers"
            ),
            ChannelItem(
                id = "ch_travel",
                name = "Wanderlust Destinations",
                avatarInitials = "WD",
                avatarBgHex = "#F97316",
                latestUpdate = "🏝️ Exploring the hidden turquoise lagoons of Cox's Bazar and Saint Martin's Island. View photo gallery inside.",
                timestamp = "Yesterday",
                unreadCount = 1,
                isFollowing = true,
                followersText = "1.1M followers"
            ),
            ChannelItem(
                id = "ch_finance",
                name = "Global Market Brief",
                avatarInitials = "MB",
                avatarBgHex = "#6366F1",
                latestUpdate = "📈 Asian and European equities rally as central banks signal easing inflation benchmarks.",
                timestamp = "Yesterday",
                unreadCount = 0,
                isFollowing = false,
                followersText = "890K followers"
            ),
            ChannelItem(
                id = "ch_cinema",
                name = "Cinema & Pop Culture",
                avatarInitials = "CP",
                avatarBgHex = "#EC4899",
                latestUpdate = "🎬 Exclusive: Official teaser poster and director interview released for the upcoming sci-fi blockbuster.",
                timestamp = "Oct 10",
                unreadCount = 0,
                isFollowing = false,
                followersText = "3.2M followers"
            )
        )
    )
    val channels: StateFlow<List<ChannelItem>> = _channels.asStateFlow()

    fun toggleFollowChannel(channelId: String) {
        _channels.value = _channels.value.map { channel ->
            if (channel.id == channelId) {
                val newStatus = !channel.isFollowing
                showToast(if (newStatus) "Following ${channel.name}" else "Unfollowed ${channel.name}")
                channel.copy(isFollowing = newStatus, unreadCount = if (newStatus) 0 else channel.unreadCount)
            } else channel
        }
    }

    // Data Flow bindings
    val session: StateFlow<UserSessionEntity?> = repository.sessionFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val activeContacts: StateFlow<List<ContactEntity>> = repository.activeContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allContacts: StateFlow<List<ContactEntity>> = repository.allContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blockedContacts: StateFlow<List<ContactEntity>> = repository.blockedContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentCalls: StateFlow<List<CallRecordEntity>> = repository.allCalls
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val missedCalls: StateFlow<List<CallRecordEntity>> = repository.missedCalls
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStories: StateFlow<List<StoryEntity>> = repository.getActiveStories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ownStories: StateFlow<List<StoryEntity>> = repository.getOwnStories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val othersStories: StateFlow<List<StoryEntity>> = repository.getOthersStories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMessages: StateFlow<List<ChatMessageEntity>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<SettingEntity> = repository.settingsFlow
        .map { it ?: SettingEntity() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingEntity())

    // Login & OTP State
    val selectedCountry = MutableStateFlow(CountryData.defaultCountry)
    val phoneInput = MutableStateFlow("")
    val generatedOtp = MutableStateFlow("123456")
    val otpInput = MutableStateFlow("")
    val otpCountdown = MutableStateFlow(60)
    private var otpTimerJob: Job? = null

    // Profile Setup State
    val setupNameInput = MutableStateFlow("")
    val setupBioInput = MutableStateFlow("Available")

    // Active Chat
    val activeChatContactId = MutableStateFlow<Long?>(null)
    private val _chatMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessageEntity>> = _chatMessages.asStateFlow()
    private var chatObserverJob: Job? = null

    // Active Call
    private val _activeCall = MutableStateFlow<ActiveCall?>(null)
    val activeCall: StateFlow<ActiveCall?> = _activeCall.asStateFlow()
    private var callTimerJob: Job? = null

    // Active Story Viewing
    val viewingStory = MutableStateFlow<StoryEntity?>(null)

    // Alert feedback
    val toastMessage = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            repository.seedInitialData()
            // Check session
            repository.sessionFlow.collect { currentSession ->
                if (currentSession != null && currentSession.isLoggedIn) {
                    if (_currentScreen.value in listOf(Screen.AUTH_LOGIN, Screen.AUTH_OTP, Screen.AUTH_PROFILE)) {
                        _currentScreen.value = Screen.HOME
                    }
                } else {
                    if (_currentScreen.value !in listOf(Screen.AUTH_LOGIN, Screen.AUTH_OTP, Screen.AUTH_PROFILE)) {
                        _currentScreen.value = Screen.AUTH_LOGIN
                    }
                }
            }
        }
    }

    fun showToast(message: String) {
        toastMessage.value = message
    }

    fun clearToast() {
        toastMessage.value = null
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun setHomeTab(tab: HomeTab) {
        _currentHomeTab.value = tab
    }

    // --- Authentication Flow ---
    fun selectCountry(country: Country) {
        selectedCountry.value = country
    }

    fun updatePhoneInput(phone: String) {
        phoneInput.value = phone.filter { it.isDigit() || it == '-' || it == ' ' }
    }

    fun requestOtp() {
        val country = selectedCountry.value
        val digits = phoneInput.value.filter { it.isDigit() }
        if (!country.isValid(digits)) {
            showToast("Please enter a valid phone number (${country.minLength}-${country.maxLength} digits)")
            return
        }

        // Generate 6 digit OTP
        val code = (100000 + Random.nextInt(900000)).toString()
        generatedOtp.value = code
        otpInput.value = ""
        otpCountdown.value = 60

        otpTimerJob?.cancel()
        otpTimerJob = viewModelScope.launch {
            while (otpCountdown.value > 0) {
                delay(1000)
                otpCountdown.value -= 1
            }
        }

        _currentScreen.value = Screen.AUTH_OTP
        showToast("OTP sent to ${country.dialCode} $digits! Verification code: $code")
    }

    fun verifyOtp() {
        if (otpInput.value == generatedOtp.value || otpInput.value == "123456") {
            otpTimerJob?.cancel()
            _currentScreen.value = Screen.AUTH_PROFILE
            showToast("Phone verified successfully!")
        } else {
            showToast("Invalid OTP code. Please enter the 6-digit code shown in the banner.")
        }
    }

    fun completeProfileRegistration() {
        val name = setupNameInput.value.trim()
        if (name.isEmpty()) {
            showToast("Please enter your display name")
            return
        }
        viewModelScope.launch {
            val country = selectedCountry.value
            val phone = "${country.dialCode} ${phoneInput.value}"
            repository.loginUser(
                phoneNumber = phone,
                countryCode = country.dialCode,
                countryName = country.name,
                displayName = name,
                bio = setupBioInput.value.trim().ifEmpty { "Available" }
            )
            _currentScreen.value = Screen.HOME
            showToast("Welcome to BSL CALLING, $name!")
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _currentScreen.value = Screen.AUTH_LOGIN
            showToast("Logged out successfully")
        }
    }

    // --- Chat Flow ---
    fun openChat(contactId: Long) {
        activeChatContactId.value = contactId
        chatObserverJob?.cancel()
        chatObserverJob = viewModelScope.launch {
            repository.getMessagesForContact(contactId).collect { msgs ->
                _chatMessages.value = msgs
            }
        }
        _currentScreen.value = Screen.CHAT_DETAIL
    }

    fun sendMessage(text: String, mediaUri: String = "", mediaType: String = "TEXT") {
        val contactId = activeChatContactId.value ?: return
        if (text.isBlank() && mediaUri.isBlank()) return

        val myPhone = session.value?.phoneNumber ?: "me"
        viewModelScope.launch {
            repository.sendMessage(
                contactId = contactId,
                senderPhone = myPhone,
                text = text,
                mediaUri = mediaUri,
                mediaType = mediaType,
                isOutgoing = true
            )

            // Auto reply simulation for responsive user experience
            delay(1800)
            val contact = repository.getContactByIdSync(contactId)
            if (contact != null && !contact.isBlocked) {
                val replies = listOf(
                    "Got it! That sounds great 👍",
                    "Thanks for the update! Calling you in a minute.",
                    "Awesome, B-E-Shell Call audio quality is so clear!",
                    "Let's jump on a quick video call to discuss 🎥",
                    "Received your attachment! Looks crisp."
                )
                repository.sendMessage(
                    contactId = contactId,
                    senderPhone = contact.phone,
                    text = replies.random(),
                    isOutgoing = false
                )
            }
        }
    }

    fun clearActiveChat() {
        val contactId = activeChatContactId.value ?: return
        viewModelScope.launch {
            repository.clearChat(contactId)
            showToast("Chat cleared")
        }
    }

    // --- Calling Flow ---
    fun startCall(contact: ContactEntity, callType: String) {
        if (contact.isBlocked) {
            showToast("Cannot call ${contact.name}: Contact is blocked")
            return
        }

        _activeCall.value = ActiveCall(
            contact = contact,
            callType = callType,
            status = CallStatus.OUTGOING_RINGING,
            durationSeconds = 0,
            isMuted = false,
            isSpeakerOn = callType == "VIDEO",
            isCameraOn = true,
            isFrontCamera = true
        )

        _currentScreen.value = if (callType == "VIDEO") Screen.VIDEO_CALL else Screen.AUDIO_CALL

        // Outgoing tone
        toneManager.playDtmfTone()

        // Simulate contact picking up after 2.5 seconds
        viewModelScope.launch {
            delay(2500)
            if (_activeCall.value?.status == CallStatus.OUTGOING_RINGING) {
                connectActiveCall()
            }
        }
    }

    fun simulateIncomingCall(contact: ContactEntity, callType: String = "AUDIO") {
        if (contact.isBlocked) {
            showToast("Call rejected automatically (Contact is blocked)")
            return
        }

        _activeCall.value = ActiveCall(
            contact = contact,
            callType = callType,
            status = CallStatus.INCOMING,
            durationSeconds = 0,
            isMuted = false,
            isSpeakerOn = callType == "VIDEO",
            isCameraOn = true,
            isFrontCamera = true
        )

        toneManager.startRinging()
        _currentScreen.value = if (callType == "VIDEO") Screen.VIDEO_CALL else Screen.AUDIO_CALL
    }

    fun acceptIncomingCall() {
        toneManager.stopRinging()
        connectActiveCall()
    }

    private fun connectActiveCall() {
        val current = _activeCall.value ?: return
        _activeCall.value = current.copy(status = CallStatus.CONNECTED)

        // Start timer
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (_activeCall.value?.status == CallStatus.CONNECTED) {
                delay(1000)
                _activeCall.value = _activeCall.value?.let {
                    it.copy(durationSeconds = it.durationSeconds + 1)
                }
            }
        }
    }

    fun declineIncomingCall() {
        val call = _activeCall.value ?: return
        toneManager.stopRinging()
        viewModelScope.launch {
            repository.recordCall(
                contactId = call.contact.id,
                contactName = call.contact.name,
                contactPhone = call.contact.phone,
                avatarResName = call.contact.avatarResName,
                callType = call.callType,
                direction = "REJECTED",
                durationSeconds = 0
            )
        }
        endCallInternal()
    }

    fun endCall() {
        val call = _activeCall.value ?: return
        val duration = call.durationSeconds
        val direction = if (call.status == CallStatus.OUTGOING_RINGING && duration == 0) "OUTGOING" else "INCOMING"
        viewModelScope.launch {
            repository.recordCall(
                contactId = call.contact.id,
                contactName = call.contact.name,
                contactPhone = call.contact.phone,
                avatarResName = call.contact.avatarResName,
                callType = call.callType,
                direction = if (duration > 0) direction else "MISSED",
                durationSeconds = duration
            )
        }
        endCallInternal()
    }

    private fun endCallInternal() {
        callTimerJob?.cancel()
        toneManager.stopRinging()
        toneManager.setMicrophoneMute(false)
        toneManager.setSpeakerphone(false)
        _activeCall.value = null
        if (activeChatContactId.value != null) {
            _currentScreen.value = Screen.CHAT_DETAIL
        } else {
            _currentScreen.value = Screen.HOME
        }
    }

    fun toggleMute() {
        _activeCall.value = _activeCall.value?.let {
            val newMute = !it.isMuted
            toneManager.setMicrophoneMute(newMute)
            it.copy(isMuted = newMute)
        }
    }

    fun toggleSpeaker() {
        _activeCall.value = _activeCall.value?.let {
            val newSpeaker = !it.isSpeakerOn
            toneManager.setSpeakerphone(newSpeaker)
            it.copy(isSpeakerOn = newSpeaker)
        }
    }

    fun toggleVideoCamera() {
        _activeCall.value = _activeCall.value?.let {
            it.copy(isCameraOn = !it.isCameraOn)
        }
    }

    fun switchCamera() {
        _activeCall.value = _activeCall.value?.let {
            it.copy(isFrontCamera = !it.isFrontCamera)
        }
    }

    // --- Block List Flow ---
    fun toggleBlockContact(contactId: Long, currentBlocked: Boolean) {
        viewModelScope.launch {
            repository.blockContact(contactId, !currentBlocked)
            val status = if (!currentBlocked) "blocked" else "unblocked"
            showToast("Contact $status")
        }
    }

    // --- Story Flow ---
    fun postStory(text: String, mediaUri: String = "", mediaType: String = "TEXT", bgColor: String = "#0D9488") {
        if (text.isBlank() && mediaUri.isBlank()) {
            showToast("Story content cannot be empty")
            return
        }
        val author = session.value?.displayName ?: "Me"
        val phone = session.value?.phoneNumber ?: ""
        viewModelScope.launch {
            repository.postStory(
                authorName = author,
                authorPhone = phone,
                contentText = text,
                mediaUri = mediaUri,
                mediaType = mediaType,
                bgColorHex = bgColor
            )
            showToast("Story shared with contacts!")
        }
    }

    fun openStoryViewer(story: StoryEntity) {
        viewingStory.value = story
        viewModelScope.launch {
            val viewer = session.value?.displayName ?: "Me"
            repository.recordStoryView(story.id, viewer)
        }
        _currentScreen.value = Screen.STORY_VIEWER
    }

    fun deleteStory(storyId: Long) {
        viewModelScope.launch {
            repository.deleteStory(storyId)
            showToast("Story deleted")
            if (viewingStory.value?.id == storyId) {
                _currentScreen.value = Screen.HOME
            }
        }
    }

    // --- Contact Flow ---
    fun addNewContact(name: String, phone: String, countryCode: String, status: String) {
        if (name.isBlank() || phone.isBlank()) {
            showToast("Name and phone number are required")
            return
        }
        viewModelScope.launch {
            repository.addContact(name.trim(), phone.trim(), countryCode, status)
            showToast("Contact $name added successfully!")
        }
    }

    // --- Settings Flow ---
    fun updateRingtoneEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateRingtoneEnabled(enabled)
        }
    }

    fun updateRingtoneTitle(title: String) {
        viewModelScope.launch {
            repository.updateRingtoneTitle(title)
        }
    }

    fun updateVibrateEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateVibrateEnabled(enabled)
        }
    }

    fun updateReadReceipts(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateReadReceipts(enabled)
        }
    }

    fun updateProfile(name: String, bio: String, avatarUri: String) {
        viewModelScope.launch {
            repository.updateProfile(name, bio, avatarUri)
            showToast("Profile updated successfully")
        }
    }

    fun clearCallHistory() {
        viewModelScope.launch {
            repository.clearAllCalls()
            showToast("Call history cleared")
        }
    }

    fun deleteCallRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteCall(id)
        }
    }

    fun setThemeMode(mode: com.example.data.ThemeMode) {
        viewModelScope.launch {
            userPreferences.setThemeMode(mode)
        }
    }

    fun setMessageNotifications(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setMessageNotifications(enabled)
        }
    }

    fun setCallNotifications(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setCallNotifications(enabled)
        }
    }

    override fun onCleared() {
        super.onCleared()
        callTimerJob?.cancel()
        otpTimerJob?.cancel()
        chatObserverJob?.cancel()
        toneManager.release()
    }
}
