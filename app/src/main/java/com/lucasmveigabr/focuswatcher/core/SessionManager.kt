package com.lucasmveigabr.focuswatcher.core

import com.lucasmveigabr.focuswatcher.core.notification.FocusNotificationManager
import com.lucasmveigabr.focuswatcher.domain.model.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
class SessionManager(
    private val notificationManager: FocusNotificationManager,
    private val coroutineScope: CoroutineScope
) {
    private val _currentSession = MutableStateFlow<Session?>(null)
    val currentSession = _currentSession.asStateFlow()
    private var counterJob: Job? = null

    fun startSession(): Boolean {
        if (isOngoing()) return false
        _currentSession.value = Session(recordTimestamp = System.currentTimeMillis())
        startCounter()
        return true
    }

    fun stopSession(): Session? {
        val finalSession = _currentSession.value
        counterJob?.cancel()
        _currentSession.value = null
        notificationManager.clearDistractionNotification()
        return finalSession
    }

    fun onNoiseDetected() {
        updateSession {
            it.copy(
                noiseEventCount = it.noiseEventCount + 1
            )
        }
        attemptNotification("Noise detected!")
    }

    fun onMovementDetected() {
        updateSession {
            it.copy(
                movementEventCount = it.movementEventCount + 1
            )
        }
        attemptNotification("Movement detected!")
    }

    fun isOngoing() = _currentSession.value != null

    private fun startCounter() {
        counterJob = coroutineScope.launch {
            while (isOngoing() && isActive) {
                updateSession {
                    it.copy(durationInMillis = System.currentTimeMillis() - it.recordTimestamp)
                }
                delay(1000L)
            }
        }
    }

    private fun attemptNotification(message: String) {
        try {
            notificationManager.showDistraction(message)
        } catch (se: SecurityException) {
            se.printStackTrace()
        }
    }

    private fun updateSession(transform: (Session) -> Session) {
        _currentSession.update { session ->
            session?.let {
                transform(it)
            }
        }
    }

}