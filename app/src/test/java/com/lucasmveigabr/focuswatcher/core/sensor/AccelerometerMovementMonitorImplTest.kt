package com.lucasmveigabr.focuswatcher.core.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.SensorEventBuilder
import org.robolectric.shadows.ShadowSensor
import org.robolectric.shadows.ShadowSensorManager

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AccelerometerMovementMonitorImplTest {

    private lateinit var context: Context
    private lateinit var monitor: AccelerometerMovementMonitorImpl
    private lateinit var shadowSensorManager: ShadowSensorManager
    private lateinit var mockSensor: Sensor

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shadowSensorManager = shadowOf(sensorManager)
        mockSensor = ShadowSensor.newInstance(Sensor.TYPE_LINEAR_ACCELERATION)
        shadowSensorManager.addSensor(mockSensor)

        monitor = AccelerometerMovementMonitorImpl(context)
    }

    @Test
    fun `onSensorChanged should emit correct magnitude`() = runTest {
        monitor.startDetection()

        monitor.observeMovement().test {
            val sensorEvent = SensorEventBuilder.newBuilder()
                .setSensor(mockSensor)
                .setValues(floatArrayOf(3f, 4f, 0f))
                .build()

            shadowSensorManager.sendSensorEventToListeners(sensorEvent)

            val item = awaitItem()
            assertEquals(5.0f, item)
        }
    }

    @Test
    fun `stopDetection should unregister listener and emit zero`() = runTest {
        monitor.startDetection()

        monitor.observeMovement().test {
            val sensorEvent = SensorEventBuilder.newBuilder()
                .setSensor(mockSensor)
                .setValues(floatArrayOf(3f, 4f, 0f))
                .build()

            shadowSensorManager.sendSensorEventToListeners(sensorEvent)
            val firstEvent = awaitItem()
            assertEquals(5.0f, firstEvent)

            monitor.stopDetection()
            assertEquals(0.0f, awaitItem())
        }
    }
}