package com.lucasmveigabr.focuswatcher.domain.usecase

import com.lucasmveigabr.focuswatcher.data.repository.SessionRepository
import com.lucasmveigabr.focuswatcher.domain.model.Session

class PersistSessionUseCase(
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke(session: Session) = runCatching {
        sessionRepository.insertSession(session)
    }

}