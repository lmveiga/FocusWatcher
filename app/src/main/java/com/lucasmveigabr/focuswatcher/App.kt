package com.lucasmveigabr.focuswatcher

import android.app.Application
import com.lucasmveigabr.focuswatcher.core.di.AppModule
import com.lucasmveigabr.focuswatcher.core.notification.FocusNotificationManager
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(AppModule.module)
        }
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        getKoin().get<FocusNotificationManager>().createNotificationChannel()
    }

}