package com.lucasmveigabr.focuswatcher.core.sensor

import kotlinx.coroutines.flow.Flow

interface AccelerometerMovementMonitor {

    fun observeMovement(): Flow<Float>

    fun startDetection()

    fun stopDetection()
}