package com.lucasmveigabr.focuswatcher.core.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNotificationManager

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33]) // Aligns with Android 13+ notification requirements [cite: 57]
class FocusNotificationManagerTest {

    private lateinit var context: Context
    private lateinit var notificationManager: FocusNotificationManager
    private lateinit var sysNotificationManager: NotificationManager
    private lateinit var shadowManager: ShadowNotificationManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        sysNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager = FocusNotificationManager(context)
        shadowManager = shadowOf(sysNotificationManager)
    }

    @Test
    fun `showDistraction should trigger notification with correct ID`() {
        notificationManager.showDistraction("Loud noise detected")

        val notification = shadowManager.getNotification(FocusNotificationManager.DISTRACTION_ID)

        assertEquals(
            "Distractions Detected",
            notification.extras.getString(Notification.EXTRA_TITLE)
        )
    }

    @Test
    fun `showDistraction should maintain a maximum of 3 history items`() {
        notificationManager.showDistraction("Event 1")
        notificationManager.showDistraction("Event 2")
        notificationManager.showDistraction("Event 3")
        notificationManager.showDistraction("Event 4")

        val notification = shadowManager.getNotification(FocusNotificationManager.DISTRACTION_ID)
        val lines = notification.extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)

        assertNotNull(lines)
        assertEquals(3, lines?.size)
        assertEquals("Event 4", lines?.get(0).toString())
        assertEquals("Event 2", lines?.get(2).toString())
    }

    @Test
    fun `getServiceNotification should return ongoing notification`() {
        val notification = notificationManager.getServiceNotification()

        val isOngoing = (notification.flags and Notification.FLAG_ONGOING_EVENT) != 0
        assertTrue(isOngoing)
        assertEquals(
            "Focus Session Active",
            notification.extras.getString(Notification.EXTRA_TITLE)
        )
    }

    @Test
    fun `clearDistractionNotification should remove notification and reset history`() {
        notificationManager.showDistraction("Noise")
        notificationManager.clearDistractionNotification()

        val notification = shadowManager.getNotification(FocusNotificationManager.DISTRACTION_ID)
        assertNull("Notification should be cancelled", notification)

        notificationManager.showDistraction("New Event")
        val newNotification = shadowManager.getNotification(FocusNotificationManager.DISTRACTION_ID)
        val lines = newNotification.extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
        assertEquals(1, lines?.size)
    }
}