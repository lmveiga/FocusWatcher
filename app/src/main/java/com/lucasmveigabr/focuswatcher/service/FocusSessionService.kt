package com.lucasmveigabr.focuswatcher.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.lucasmveigabr.focuswatcher.core.SessionManager
import com.lucasmveigabr.focuswatcher.core.notification.FocusNotificationManager
import com.lucasmveigabr.focuswatcher.core.sensor.AccelerometerMovementMonitor
import com.lucasmveigabr.focuswatcher.core.sensor.NoiseMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

const val NOISE_THRESHOLD = 2000
const val MOVEMENT_THRESHOLD = 5f

class FocusSessionService : Service() {

    private val noiseMonitor: NoiseMonitor by inject()
    private val movementMonitor: AccelerometerMovementMonitor by inject()
    private val sessionManager: SessionManager by inject()
    private val focusNotificationManager: FocusNotificationManager by inject()
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var isRunning = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (isRunning) return START_STICKY
        isRunning = true

        initializeService()
        setupMonitoring()
        startDetection()

        return START_STICKY
    }

    private fun initializeService() {
        val notification = focusNotificationManager.getServiceNotification()
        startForeground(FocusNotificationManager.SERVICE_ID, notification)
    }

    private fun startDetection() {
        noiseMonitor.startDetection()
        movementMonitor.startDetection()
    }

    private fun setupMonitoring() {
        noiseMonitor.observeNoiseLevel()
            .filter { it > NOISE_THRESHOLD }
            .throttleFirst(3000L)
            .onEach { _ ->
                sessionManager.onNoiseDetected()
            }.launchIn(serviceScope)

        movementMonitor.observeMovement()
            .filter { it > MOVEMENT_THRESHOLD }
            .throttleFirst(3000L)
            .onEach { _ ->
                sessionManager.onMovementDetected()
            }.launchIn(serviceScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        noiseMonitor.stopDetection()
        movementMonitor.stopDetection()
        serviceScope.cancel()
        sessionManager.stopSession()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}

fun <T> Flow<T>.throttleFirst(windowMillis: Long): Flow<T> = flow {
    var lastEmissionTime = 0L
    collect { value ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEmissionTime > windowMillis) {
            lastEmissionTime = currentTime
            emit(value)
        }
    }
}
