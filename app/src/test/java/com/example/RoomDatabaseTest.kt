package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.model.CallRecordEntity
import com.example.model.ChatMessageEntity
import com.example.model.ContactEntity
import com.example.model.UserSessionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomDatabaseTest {
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testUserProfileSessionDao() = runBlocking {
        val sessionDao = db.userSessionDao()

        // 1. Insert Profile Session
        val user = UserSessionEntity(
            id = 1,
            phoneNumber = "+880 1712-345678",
            countryCode = "+880",
            countryName = "Bangladesh",
            displayName = "Tariq Ahmed",
            bio = "Available for calls",
            isLoggedIn = true
        )
        sessionDao.insertSession(user)

        // 2. Query Profile
        val saved = sessionDao.getSession().first()
        assertNotNull(saved)
        assertEquals("Tariq Ahmed", saved?.displayName)
        assertTrue(saved?.isLoggedIn == true)

        // 3. Update Profile
        sessionDao.updateProfile("Tariq A. Chief", "In a video call", "new_avatar_uri")
        val updated = sessionDao.getSession().first()
        assertEquals("Tariq A. Chief", updated?.displayName)
        assertEquals("In a video call", updated?.bio)

        // 4. Logout
        sessionDao.logout()
        val loggedOut = sessionDao.getSession().first()
        assertFalse(loggedOut?.isLoggedIn == true)
    }

    @Test
    fun testMessageHistoryDao() = runBlocking {
        val chatDao = db.chatDao()

        // 1. Insert Messages
        val msg1 = ChatMessageEntity(
            contactId = 10,
            senderPhone = "+8801712345678",
            text = "Hello from BSL CALLING!",
            status = "SENT",
            isOutgoing = true
        )
        val msg2 = ChatMessageEntity(
            contactId = 10,
            senderPhone = "+919876543210",
            text = "Received loud and clear!",
            status = "READ",
            isOutgoing = false
        )
        val id1 = chatDao.insertMessage(msg1)
        val id2 = chatDao.insertMessage(msg2)
        assertTrue(id1 > 0)
        assertTrue(id2 > 0)

        // 2. Query Conversation for Contact 10
        val messages = chatDao.getMessagesForContact(10).first()
        assertEquals(2, messages.size)

        // 3. Search Messages
        val searchResults = chatDao.searchMessages("clear").first()
        assertEquals(1, searchResults.size)
        assertEquals("Received loud and clear!", searchResults[0].text)

        // 4. Update Status to READ
        chatDao.updateStatus(id1, "READ")
        val updatedMsgs = chatDao.getMessagesForContact(10).first()
        val updatedFirst = updatedMsgs.find { it.id == id1 }
        assertEquals("READ", updatedFirst?.status)

        // 5. Clear conversation
        chatDao.clearChatForContact(10)
        val emptyMsgs = chatDao.getMessagesForContact(10).first()
        assertTrue(emptyMsgs.isEmpty())
    }

    @Test
    fun testCallLogDao() = runBlocking {
        val callDao = db.callDao()

        // 1. Insert Call Records
        val call1 = CallRecordEntity(
            contactId = 5,
            contactName = "Sophia Miller",
            contactPhone = "+880 1712-345678",
            callType = "AUDIO",
            direction = "INCOMING",
            durationSeconds = 120
        )
        val call2 = CallRecordEntity(
            contactId = 6,
            contactName = "Alex Rodriguez",
            contactPhone = "+91 98765-43210",
            callType = "VIDEO",
            direction = "MISSED",
            durationSeconds = 0
        )
        callDao.insertCall(call1)
        callDao.insertCall(call2)

        // 2. Query All Calls
        val all = callDao.getAllCalls().first()
        assertEquals(2, all.size)

        // 3. Query Missed Calls
        val missed = callDao.getMissedCalls().first()
        assertEquals(1, missed.size)
        assertEquals("Alex Rodriguez", missed[0].contactName)
        assertEquals("MISSED", missed[0].direction)

        // 4. Query Calls by Contact
        val sophiaCalls = callDao.getCallsByContact(5).first()
        assertEquals(1, sophiaCalls.size)
        assertEquals(120, sophiaCalls[0].durationSeconds)

        // 5. Clear All Calls
        callDao.clearAllCalls()
        val emptyCalls = callDao.getAllCalls().first()
        assertTrue(emptyCalls.isEmpty())
    }
}
