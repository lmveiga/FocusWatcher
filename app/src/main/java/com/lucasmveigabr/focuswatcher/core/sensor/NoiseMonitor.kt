package com.lucasmveigabr.focuswatcher.core.sensor

import kotlinx.coroutines.flow.Flow

interface NoiseMonitor {

    fun observeNoiseLevel(): Flow<Int>

    fun startDetection()
    fun stopDetection()

}