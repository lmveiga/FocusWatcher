package com.lucasmveigabr.focuswatcher.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lucasmveigabr.focuswatcher.data.database.dao.SessionDao
import com.lucasmveigabr.focuswatcher.data.database.entity.SessionEntity

@Database(entities = [SessionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}