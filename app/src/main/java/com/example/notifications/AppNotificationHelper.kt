package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class AppNotificationHelper(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_MESSAGES = "beshell_channel_messages"
        const val CHANNEL_CALLS = "beshell_channel_calls"
        private const val NOTIFICATION_ID_MSG = 1001
        private const val NOTIFICATION_ID_CALL = 2001
    }

    init {
        createChannels()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val messageChannel = NotificationChannel(
                CHANNEL_MESSAGES,
                "New Messages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for incoming private messages"
                enableVibration(true)
            }

            val callChannel = NotificationChannel(
                CHANNEL_CALLS,
                "Incoming Calls",
                NotificationManager.IMPORTANCE_MAX
            ).apply {
                description = "Heads-up alerts for incoming audio and video calls"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(messageChannel)
            notificationManager.createNotificationChannel(callChannel)
        }
    }

    fun showIncomingMessageNotification(senderName: String, messageText: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_MESSAGES)
            .setSmallIcon(R.drawable.ic_launcher_beshell)
            .setContentTitle(senderName)
            .setContentText(messageText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID_MSG + (System.currentTimeMillis() % 100).toInt(), notification)
    }

    fun showIncomingCallNotification(callerName: String, isVideo: Boolean) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val callTypeStr = if (isVideo) "Incoming Video Call" else "Incoming Audio Call"
        val notification = NotificationCompat.Builder(context, CHANNEL_CALLS)
            .setSmallIcon(R.drawable.ic_launcher_beshell)
            .setContentTitle(callerName)
            .setContentText(callTypeStr)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_CALL, notification)
    }

    fun cancelCallNotification() {
        notificationManager.cancel(NOTIFICATION_ID_CALL)
    }
}
