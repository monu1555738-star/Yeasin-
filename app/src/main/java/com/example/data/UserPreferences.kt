package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class UserPreferencesConfig(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val messageNotifications: Boolean = true,
    val callNotifications: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val readReceipts: Boolean = true
)

class UserPreferences(private val context: Context) {

    companion object {
        val KEY_THEME_MODE = stringPreferencesKey("key_theme_mode")
        val KEY_NOTIFY_MESSAGES = booleanPreferencesKey("key_notify_messages")
        val KEY_NOTIFY_CALLS = booleanPreferencesKey("key_notify_calls")
        val KEY_SOUND_ENABLED = booleanPreferencesKey("key_sound_enabled")
        val KEY_VIBRATION_ENABLED = booleanPreferencesKey("key_vibration_enabled")
        val KEY_READ_RECEIPTS = booleanPreferencesKey("key_read_receipts")
    }

    val userPreferencesFlow: Flow<UserPreferencesConfig> = context.dataStore.data
        .map { preferences ->
            val themeStr = preferences[KEY_THEME_MODE] ?: ThemeMode.SYSTEM.name
            val themeMode = try {
                ThemeMode.valueOf(themeStr)
            } catch (e: Exception) {
                ThemeMode.SYSTEM
            }

            UserPreferencesConfig(
                themeMode = themeMode,
                messageNotifications = preferences[KEY_NOTIFY_MESSAGES] ?: true,
                callNotifications = preferences[KEY_NOTIFY_CALLS] ?: true,
                soundEnabled = preferences[KEY_SOUND_ENABLED] ?: true,
                vibrationEnabled = preferences[KEY_VIBRATION_ENABLED] ?: true,
                readReceipts = preferences[KEY_READ_RECEIPTS] ?: true
            )
        }

    val themeModeFlow: Flow<ThemeMode> = userPreferencesFlow.map { it.themeMode }
    val messageNotificationsFlow: Flow<Boolean> = userPreferencesFlow.map { it.messageNotifications }
    val callNotificationsFlow: Flow<Boolean> = userPreferencesFlow.map { it.callNotifications }
    val soundEnabledFlow: Flow<Boolean> = userPreferencesFlow.map { it.soundEnabled }
    val vibrationEnabledFlow: Flow<Boolean> = userPreferencesFlow.map { it.vibrationEnabled }
    val readReceiptsFlow: Flow<Boolean> = userPreferencesFlow.map { it.readReceipts }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = themeMode.name
        }
    }

    suspend fun setMessageNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFY_MESSAGES] = enabled
        }
    }

    suspend fun setCallNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFY_CALLS] = enabled
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SOUND_ENABLED] = enabled
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun setReadReceipts(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_READ_RECEIPTS] = enabled
        }
    }
}
