package com.lucasmveigabr.focuswatcher.domain.usecase

import com.lucasmveigabr.focuswatcher.domain.repository.SessionRepository
import com.lucasmveigabr.focuswatcher.domain.model.Session
import kotlinx.coroutines.flow.Flow

class RetrieveSessionsUseCase(
    private val sessionRepository: SessionRepository
) {

    operator fun invoke(): Flow<List<Session>> =
        sessionRepository.sessionsFlow()

}