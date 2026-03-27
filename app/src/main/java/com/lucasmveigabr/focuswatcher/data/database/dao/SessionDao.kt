package com.lucasmveigabr.focuswatcher.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lucasmveigabr.focuswatcher.data.database.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM session ORDER BY recordTimestamp DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sessions: List<SessionEntity>)

    @Query("UPDATE session SET isSynced = 1 WHERE id = :sessionId")
    suspend fun markAsSynced(sessionId: Int)

    @Query("SELECT * FROM session WHERE isSynced = 0")
    suspend fun getUnsyncedSessions(): List<SessionEntity>
}