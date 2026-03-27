package com.lucasmveigabr.focuswatcher.core.sensor

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

class NoiseMonitorImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NoiseMonitor {

    private var audioRecord: AudioRecord? = null
    private var monitoringJob: Job? = null

    private val _noiseFlow = MutableSharedFlow<Int>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun observeNoiseLevel(): Flow<Int> = _noiseFlow

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun startDetection() {
        if (monitoringJob?.isActive == true) return

        val bufferSize = AudioRecord.getMinBufferSize(
            44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
        audioRecord?.startRecording()

        monitoringJob = CoroutineScope(dispatcher).launch {
            val buffer = ShortArray(bufferSize)
            while (isActive) {
                val readResult = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (readResult > 0) {
                    var max = 0
                    for (i in 0 until readResult) {
                        val value = abs(buffer[i].toInt())
                        if (value > max) max = value
                    }
                    _noiseFlow.tryEmit(max)
                }
                delay(150)
            }
        }

    }

    override fun stopDetection() {
        monitoringJob?.cancel()
        monitoringJob = null
        audioRecord?.apply {
            stop()
            release()
        }
        audioRecord = null
        _noiseFlow.tryEmit(0)
    }
}