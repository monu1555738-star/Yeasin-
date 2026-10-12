package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CallRecordEntity
import com.example.model.ChatMessageEntity
import com.example.model.ContactEntity
import com.example.model.SettingEntity
import com.example.model.StoryEntity
import com.example.model.UserSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSessionDao {
    @Query("SELECT * FROM user_session WHERE id = 1 LIMIT 1")
    fun getSession(): Flow<UserSessionEntity?>

    @Query("SELECT * FROM user_session WHERE id = 1 LIMIT 1")
    suspend fun getSessionSync(): UserSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: UserSessionEntity)

    @Query("UPDATE user_session SET isLoggedIn = 0 WHERE id = 1")
    suspend fun logout()

    @Query("UPDATE user_session SET displayName = :name, bio = :bio, avatarUri = :avatarUri WHERE id = 1")
    suspend fun updateProfile(name: String, bio: String, avatarUri: String)
}

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts WHERE isBlocked = 0 ORDER BY name ASC")
    fun getActiveContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE isBlocked = 1 ORDER BY name ASC")
    fun getBlockedContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE id = :id LIMIT 1")
    fun getContactById(id: Long): Flow<ContactEntity?>

    @Query("SELECT * FROM contacts WHERE id = :id LIMIT 1")
    suspend fun getContactByIdSync(id: Long): ContactEntity?

    @Query("SELECT COUNT(*) FROM contacts")
    suspend fun getContactCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactEntity>)

    @Query("UPDATE contacts SET isBlocked = :isBlocked WHERE id = :id")
    suspend fun setBlocked(id: Long, isBlocked: Boolean)

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteContact(id: Long)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE contactId = :contactId ORDER BY timestamp ASC")
    fun getMessagesForContact(contactId: Long): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE text LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchMessages(query: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Query("UPDATE chat_messages SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteMessageById(id: Long)

    @Query("DELETE FROM chat_messages WHERE contactId = :contactId")
    suspend fun clearChatForContact(contactId: Long)
}

@Dao
interface CallDao {
    @Query("SELECT * FROM call_records ORDER BY timestamp DESC")
    fun getAllCalls(): Flow<List<CallRecordEntity>>

    @Query("SELECT * FROM call_records WHERE direction = 'MISSED' ORDER BY timestamp DESC")
    fun getMissedCalls(): Flow<List<CallRecordEntity>>

    @Query("SELECT * FROM call_records WHERE contactId = :contactId ORDER BY timestamp DESC")
    fun getCallsByContact(contactId: Long): Flow<List<CallRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCall(call: CallRecordEntity): Long

    @Query("DELETE FROM call_records WHERE id = :id")
    suspend fun deleteCall(id: Long)

    @Query("DELETE FROM call_records")
    suspend fun clearAllCalls()
}

@Dao
interface StoryDao {
    // 24 hours = 24 * 60 * 60 * 1000 = 86400000 ms
    @Query("SELECT * FROM stories WHERE timestamp > :cutoffTimestamp ORDER BY timestamp DESC")
    fun getActiveStories(cutoffTimestamp: Long): Flow<List<StoryEntity>>

    @Query("SELECT * FROM stories WHERE isOwnStory = 1 AND timestamp > :cutoffTimestamp ORDER BY timestamp DESC")
    fun getOwnStories(cutoffTimestamp: Long): Flow<List<StoryEntity>>

    @Query("SELECT * FROM stories WHERE isOwnStory = 0 AND timestamp > :cutoffTimestamp ORDER BY timestamp DESC")
    fun getOthersStories(cutoffTimestamp: Long): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity): Long

    @Query("DELETE FROM stories WHERE id = :id")
    suspend fun deleteStory(id: Long)

    @Query("UPDATE stories SET viewsCount = viewsCount + 1, viewerNames = CASE WHEN viewerNames = '' THEN :viewerName ELSE viewerNames || ', ' || :viewerName END WHERE id = :id")
    suspend fun recordStoryView(id: Long, viewerName: String)

    @Query("SELECT COUNT(*) FROM stories WHERE timestamp > :cutoffTimestamp")
    suspend fun getActiveStoryCount(cutoffTimestamp: Long): Int
}

@Dao
interface SettingDao {
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<SettingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SettingEntity)

    @Query("UPDATE app_settings SET ringtoneEnabled = :enabled WHERE id = 1")
    suspend fun setRingtoneEnabled(enabled: Boolean)

    @Query("UPDATE app_settings SET ringtoneTitle = :title WHERE id = 1")
    suspend fun setRingtoneTitle(title: String)

    @Query("UPDATE app_settings SET vibrateEnabled = :enabled WHERE id = 1")
    suspend fun setVibrateEnabled(enabled: Boolean)

    @Query("UPDATE app_settings SET readReceiptsEnabled = :enabled WHERE id = 1")
    suspend fun setReadReceiptsEnabled(enabled: Boolean)
}
