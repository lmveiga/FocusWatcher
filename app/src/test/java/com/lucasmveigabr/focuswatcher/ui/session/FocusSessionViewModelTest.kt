package com.lucasmveigabr.focuswatcher.ui.session

import app.cash.turbine.test
import com.lucasmveigabr.focuswatcher.core.LoggerProtocol
import com.lucasmveigabr.focuswatcher.core.SessionManager
import com.lucasmveigabr.focuswatcher.domain.model.Session
import com.lucasmveigabr.focuswatcher.domain.usecase.FetchSessionsUseCase
import com.lucasmveigabr.focuswatcher.domain.usecase.PersistSessionUseCase
import com.lucasmveigabr.focuswatcher.domain.usecase.RetrieveSessionsUseCase
import com.lucasmveigabr.focuswatcher.domain.usecase.SyncSessionsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FocusSessionViewModelTest {

    private val sessionManager: SessionManager = mockk(relaxed = true)
    private val persistSessionUseCase: PersistSessionUseCase = mockk(relaxed = true)
    private val retrieveSessionsUseCase: RetrieveSessionsUseCase = mockk(relaxed = true)
    private val fetchSessionsUseCase: FetchSessionsUseCase = mockk(relaxed = true)
    private val syncSessionsUseCase: SyncSessionsUseCase = mockk(relaxed = true)
    private val loggerProtocol: LoggerProtocol = mockk(relaxed = true)

    private lateinit var viewModel: FocusSessionViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { retrieveSessionsUseCase() } returns flowOf(emptyList())
        coEvery { persistSessionUseCase(any()) } returns Result.success(Unit)

        viewModel = FocusSessionViewModel(
            sessionManager,
            persistSessionUseCase,
            retrieveSessionsUseCase,
            fetchSessionsUseCase,
            syncSessionsUseCase,
            loggerProtocol
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onToggleSessionClick starts session and triggers StartService effect`() = runTest {
        every { sessionManager.isOngoing() } returns false

        viewModel.screenEffects.test {
            viewModel.onToggleSessionClick()
            runCurrent()

            verify { sessionManager.startSession() }
            assertEquals(FocusSessionScreenEvents.StartService, awaitItem())
        }
    }

    @Test
    fun `onToggleSessionClick stops session and persists data`() = runTest {
        val mockSession = Session(recordTimestamp = 1000L, durationInMillis = 5000L)
        every { sessionManager.isOngoing() } returns true
        every { sessionManager.stopSession() } returns mockSession
        viewModel.onToggleSessionClick()

        viewModel.onToggleSessionClick()
        runCurrent()

        verify { sessionManager.stopSession() }
        coVerify { persistSessionUseCase(mockSession) }
    }

    @Test
    fun `onActivityCreated updates UI state when session is ongoing`() = runTest {
        val activeSession = Session(
            recordTimestamp = System.currentTimeMillis(),
            durationInMillis = 2000L,
            noiseEventCount = 3
        )
        every { sessionManager.isOngoing() } returns true
        every { sessionManager.currentSession } returns MutableStateFlow(activeSession)

        viewModel.screenState.test {
            viewModel.onActivityCreated()
            runCurrent()

            val state = expectMostRecentItem() as FocusSessionScreenState.Success
            assertTrue(state.isStarted)
            assertEquals(3, state.noiseDistractionCount)

            cancelAndIgnoreRemainingEvents()
        }
    }
}