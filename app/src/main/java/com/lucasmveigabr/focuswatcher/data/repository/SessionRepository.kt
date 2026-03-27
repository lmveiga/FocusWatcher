package com.lucasmveigabr.focuswatcher.data.repository

import com.lucasmveigabr.focuswatcher.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    fun sessionsFlow(): Flow<List<Session>>
    suspend fun refreshSessions()
    suspend fun insertSession(session: Session)

    suspend fun syncSessions()

}