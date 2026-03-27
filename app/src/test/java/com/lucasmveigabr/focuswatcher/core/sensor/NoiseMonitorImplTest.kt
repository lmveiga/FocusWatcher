package com.lucasmveigabr.focuswatcher.core.sensor

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class NoiseMonitorImplTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var monitor: NoiseMonitorImpl

    @Before
    fun setup() {
        monitor = NoiseMonitorImpl(testDispatcher)
    }

    @Test
    fun `noise monitor should emit reset value on stop`() = runTest(testDispatcher) {
        monitor.observeNoiseLevel().test {
            monitor.startDetection()
            monitor.stopDetection()

            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

}