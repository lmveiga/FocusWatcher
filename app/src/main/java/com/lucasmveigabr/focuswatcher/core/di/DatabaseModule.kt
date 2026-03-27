package com.lucasmveigabr.focuswatcher.core.di

import androidx.room.Room
import com.lucasmveigabr.focuswatcher.data.database.AppDatabase
import com.lucasmveigabr.focuswatcher.data.database.dao.SessionDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

object DatabaseModule {

    val module = module {
        single<AppDatabase> {
            Room.databaseBuilder(
                androidApplication(),
                AppDatabase::class.java,
                "session_database"
            ).build()
        }

        single<SessionDao> {
            get<AppDatabase>().sessionDao()
        }
    }

}