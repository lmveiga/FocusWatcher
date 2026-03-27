package com.lucasmveigabr.focuswatcher.core

import app.cash.turbine.test
import com.lucasmveigabr.focuswatcher.core.notification.FocusNotificationManager
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionManagerTest {

    private val testScope = TestScope(StandardTestDispatcher())
    private val notificationManager: FocusNotificationManager = mockk(relaxed = true)
    private lateinit var sessionManager: SessionManager

    @Before
    fun setUp() {
        sessionManager = SessionManager(notificationManager, testScope)
    }

    @Test
    fun `onNoiseDetected increments count and triggers notification`() = testScope.runTest {
        sessionManager.startSession()

        sessionManager.currentSession.test {
            skipItems(1)

            sessionManager.onNoiseDetected()

            val session = awaitItem()
            assertEquals(1, session?.noiseEventCount)
            verify { notificationManager.showDistraction("Noise detected!") }

            cancelAndIgnoreRemainingEvents()
            sessionManager.stopSession()
        }
    }

    @Test
    fun `onMovementDetected increments count and triggers notification`() = testScope.runTest {
        sessionManager.startSession()

        sessionManager.currentSession.test {
            skipItems(1)

            sessionManager.onMovementDetected()

            val session = awaitItem()
            assertEquals(1, session?.movementEventCount)
            verify { notificationManager.showDistraction("Movement detected!") }

            cancelAndIgnoreRemainingEvents()
            sessionManager.stopSession()
        }
    }

    @Test
    fun `stopSession clears state and cancels counter job`() = testScope.runTest {
        sessionManager.startSession()

        sessionManager.stopSession()

        advanceUntilIdle()
        assertNull(sessionManager.currentSession.value)
        verify { notificationManager.clearDistractionNotification() }
    }


    @Test
    fun `given no active session isOngoing returns false`() = testScope.runTest {
        assertFalse(sessionManager.isOngoing())
    }

    @Test
    fun `given active session isOngoing returns false`() = testScope.runTest {
        sessionManager.startSession()
        assertTrue(sessionManager.isOngoing())
        sessionManager.stopSession()
    }

    @Test
    fun `given running session startSession returns false`() = testScope.runTest {
        assertTrue(sessionManager.startSession()) // initial session, should be true

        assertFalse(sessionManager.startSession())
        sessionManager.stopSession()
    }

    @Test
    fun `stopSession returns session correctly`() = testScope.runTest {
        sessionManager.startSession()

        sessionManager.onNoiseDetected()
        sessionManager.onMovementDetected()
        sessionManager.onMovementDetected()

        val session = sessionManager.stopSession()
        assertEquals(2, session?.movementEventCount)
        assertEquals(1, session?.noiseEventCount)
    }
}