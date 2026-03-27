package com.lucasmveigabr.focuswatcher.core.di

import android.util.Log
import com.lucasmveigabr.focuswatcher.core.LoggerProtocol
import com.lucasmveigabr.focuswatcher.core.SessionManager
import com.lucasmveigabr.focuswatcher.core.notification.FocusNotificationManager
import com.lucasmveigabr.focuswatcher.core.sensor.AccelerometerMovementMonitor
import com.lucasmveigabr.focuswatcher.core.sensor.AccelerometerMovementMonitorImpl
import com.lucasmveigabr.focuswatcher.core.sensor.NoiseMonitor
import com.lucasmveigabr.focuswatcher.core.sensor.NoiseMonitorImpl
import com.lucasmveigabr.focuswatcher.data.network.SessionService
import com.lucasmveigabr.focuswatcher.data.network.SessionServiceMockImpl
import com.lucasmveigabr.focuswatcher.domain.repository.SessionRepository
import com.lucasmveigabr.focuswatcher.data.repository.SessionRepositoryImpl
import com.lucasmveigabr.focuswatcher.domain.usecase.FetchSessionsUseCase
import com.lucasmveigabr.focuswatcher.domain.usecase.PersistSessionUseCase
import com.lucasmveigabr.focuswatcher.domain.usecase.RetrieveSessionsUseCase
import com.lucasmveigabr.focuswatcher.domain.usecase.SyncSessionsUseCase
import com.lucasmveigabr.focuswatcher.ui.session.FocusSessionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object AppModule {

    val module = module {
        single<NoiseMonitor> {
            NoiseMonitorImpl()
        }
        single<AccelerometerMovementMonitor> {
            AccelerometerMovementMonitorImpl(androidApplication())
        }

        single {
            SessionManager(get(), CoroutineScope(SupervisorJob() + Dispatchers.Default))
        }
        singleOf(::FocusNotificationManager)

        viewModelOf(::FocusSessionViewModel)

        singleOf(::PersistSessionUseCase)
        singleOf(::RetrieveSessionsUseCase)
        singleOf(::SyncSessionsUseCase)
        singleOf(::FetchSessionsUseCase)

        single<SessionRepository> {
            SessionRepositoryImpl(get(), get(), CoroutineScope(SupervisorJob() + Dispatchers.IO))
        }

        single<SessionService> {
            SessionServiceMockImpl()
        }

        single<LoggerProtocol> {
            object : LoggerProtocol {
                override fun d(tag: String, message: String) {
                    Log.d(tag, message)
                }

                override fun e(
                    tag: String,
                    message: String,
                    throwable: Throwable
                ) {
                    Log.e(tag, message, throwable)
                }
            }
        }

    } + DatabaseModule.module
}