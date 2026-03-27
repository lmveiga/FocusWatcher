package com.lucasmveigabr.focuswatcher.domain.usecase

import com.lucasmveigabr.focuswatcher.domain.repository.SessionRepository

class SyncSessionsUseCase(
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke() = runCatching {
        sessionRepository.syncSessions()
    }
}