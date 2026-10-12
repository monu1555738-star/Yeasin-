package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ThemeMode
import com.example.data.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class UserPreferencesTest {
    private lateinit var userPreferences: UserPreferences

    @Before
    fun setup() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        userPreferences = UserPreferences(context)
        userPreferences.setThemeMode(ThemeMode.SYSTEM)
        userPreferences.setMessageNotifications(true)
        userPreferences.setCallNotifications(true)
    }

    @Test
    fun testDefaultPreferences() = runBlocking {
        val config = userPreferences.userPreferencesFlow.first()
        assertEquals(ThemeMode.SYSTEM, config.themeMode)
        assertTrue(config.messageNotifications)
        assertTrue(config.callNotifications)
    }

    @Test
    fun testUpdateThemeMode() = runBlocking {
        userPreferences.setThemeMode(ThemeMode.DARK)
        val mode = userPreferences.themeModeFlow.first()
        assertEquals(ThemeMode.DARK, mode)

        userPreferences.setThemeMode(ThemeMode.LIGHT)
        val modeLight = userPreferences.themeModeFlow.first()
        assertEquals(ThemeMode.LIGHT, modeLight)
    }

    @Test
    fun testNotificationToggles() = runBlocking {
        userPreferences.setMessageNotifications(false)
        val msgNotify = userPreferences.messageNotificationsFlow.first()
        assertFalse(msgNotify)

        userPreferences.setCallNotifications(false)
        val callNotify = userPreferences.callNotificationsFlow.first()
        assertFalse(callNotify)
    }
}
