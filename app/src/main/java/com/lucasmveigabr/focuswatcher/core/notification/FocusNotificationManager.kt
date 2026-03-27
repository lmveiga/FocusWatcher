package com.lucasmveigabr.focuswatcher.core.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lucasmveigabr.focuswatcher.MainActivity
import com.lucasmveigabr.focuswatcher.R

class FocusNotificationManager(
    private val appContext: Context
) {

    private val notificationManager = NotificationManagerCompat.from(appContext)
    private val distractionsHistory = mutableListOf<String>()

    companion object {
        const val CHANNEL_ID = "focus_session_channel"
        const val SERVICE_ID = 1
        const val DISTRACTION_ID = 2
        const val MAX_HISTORY = 3
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showDistraction(message: String) {
        distractionsHistory.add(0, message)
        if (distractionsHistory.size > MAX_HISTORY) {
            distractionsHistory.removeAt(distractionsHistory.lastIndex)
        }

        val inboxStyle = NotificationCompat.InboxStyle()
        distractionsHistory.forEach { inboxStyle.addLine(it) }

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setContentTitle("Distractions Detected")
            .setContentText("Check your latest activity")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(inboxStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntentForNotification())
            .setSilent(false)
            .build()

        notificationManager.notify(DISTRACTION_ID, notification)
    }

    fun getServiceNotification(): Notification {
        return NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setContentTitle("Focus Session Active")
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(getPendingIntentForNotification())
            .build()
    }

    fun clearDistractionNotification() {
        notificationManager.cancel(DISTRACTION_ID)
        distractionsHistory.clear()
    }

    fun createNotificationChannel() {
        val focusChannel = NotificationChannel(
            CHANNEL_ID,
            "Focus Session",
            NotificationManager.IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannels(listOf(focusChannel))
    }

    private fun getPendingIntentForNotification() =
        PendingIntent.getActivity(
            appContext,
            0,
            getFocusAppIntent(),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun getFocusAppIntent() = Intent(appContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
}