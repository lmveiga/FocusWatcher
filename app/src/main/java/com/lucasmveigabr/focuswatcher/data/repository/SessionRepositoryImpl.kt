package com.lucasmveigabr.focuswatcher.data.repository

import android.util.Log
import com.lucasmveigabr.focuswatcher.data.database.dao.SessionDao
import com.lucasmveigabr.focuswatcher.data.network.SessionService
import com.lucasmveigabr.focuswatcher.data.network.dto.PostSessionRequestDto
import com.lucasmveigabr.focuswatcher.data.network.dto.toDto
import com.lucasmveigabr.focuswatcher.domain.model.Session
import com.lucasmveigabr.focuswatcher.domain.model.toDto
import com.lucasmveigabr.focuswatcher.domain.model.toEntity
import com.lucasmveigabr.focuswatcher.domain.repository.SessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class SessionRepositoryImpl(
    private val sessionDao: SessionDao,
    private val sessionService: SessionService,
    private val coroutineScope: CoroutineScope
) : SessionRepository {

    companion object {
        const val TAG = "SessionRepository"
    }

    override fun sessionsFlow(): Flow<List<Session>> {
        return sessionDao.getAllSessions().map { sessions -> sessions.map { it.toDomain() } }
    }

    override suspend fun refreshSessions() {
        coroutineScope.launch {
            runCatching {
                sessionService.getAll()
            }.fold(
                onSuccess = { sessions ->
                    sessionDao.insertAll(sessions.map { it.toEntity() })
                },
                onFailure = {
                    Log.e(TAG, "Failure fetching sessions from API", it)
                }
            )
        }
    }

    override suspend fun insertSession(session: Session) {
        val entity = session.toEntity()
        sessionDao.insertSession(entity)

        coroutineScope.launch {
            runCatching {
                sessionService.syncSessions(
                    PostSessionRequestDto(
                        sessions = listOf(session.toDto())
                    )
                )
            }
        }
    }

    override suspend fun syncSessions() {
        coroutineScope.launch {
            runCatching {
                val sessions = sessionDao.getAllSessions().firstOrNull() ?: return@launch
                sessionService.syncSessions(
                    PostSessionRequestDto(
                        sessions = sessions.map { it.toDto() }
                    ))
            }
        }
    }

}