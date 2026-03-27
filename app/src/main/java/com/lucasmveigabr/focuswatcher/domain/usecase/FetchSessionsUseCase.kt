package com.lucasmveigabr.focuswatcher.domain.usecase

import com.lucasmveigabr.focuswatcher.data.repository.SessionRepository

class FetchSessionsUseCase(
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke() = sessionRepository.refreshSessions()

}