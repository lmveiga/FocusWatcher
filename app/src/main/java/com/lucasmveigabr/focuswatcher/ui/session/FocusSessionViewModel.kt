package com.lucasmveigabr.focuswatcher.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasmveigabr.focuswatcher.core.LoggerProtocol
import com.lucasmveigabr.focuswatcher.core.SessionManager
import com.lucasmveigabr.focuswatcher.core.util.TimeFormatter
import com.lucasmveigabr.focuswatcher.domain.model.Session
import com.lucasmveigabr.focuswatcher.domain.usecase.FetchSessionsUseCase
import com.lucasmveigabr.focuswatcher.domain.usecase.PersistSessionUseCase
import com.lucasmveigabr.focuswatcher.domain.usecase.RetrieveSessionsUseCase
import com.lucasmveigabr.focuswatcher.domain.usecase.SyncSessionsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FocusSessionViewModel(
    private val sessionManager: SessionManager,
    private val persistSessionUseCase: PersistSessionUseCase,
    private val retrieveSessionsUseCase: RetrieveSessionsUseCase,
    private val fetchSessionsUseCase: FetchSessionsUseCase,
    private val syncSessionsUseCase: SyncSessionsUseCase,
    private val logger: LoggerProtocol
) : ViewModel() {

    init {
        viewModelScope.launch {
            retrieveSessionsUseCase().distinctUntilChanged().collect { sessions ->
                updateState {
                    copy(previousRecordings = sessions.map { it.toUiModel() })
                }
            }
        }
        viewModelScope.launch {
            fetchSessionsUseCase()
        }
        viewModelScope.launch {
            syncSessionsUseCase()
        }
    }

    private val _screenState = MutableStateFlow<FocusSessionScreenState>(
        FocusSessionScreenState.Success()
    )
    val screenState = _screenState.asStateFlow()

    private val _screenEffects = Channel<FocusSessionScreenEvents>(capacity = 0)
    val screenEffects = _screenEffects.consumeAsFlow()

    fun onToggleSessionClick() {
        val currentState = _screenState.value
        if (currentState !is FocusSessionScreenState.Success) return

        if (currentState.isStarted) {
            val session = stopTracking()
            persistSession(session)
            updateState {
                copy(
                    isStarted = false,
                    formattedTime = TimeFormatter.format(session.durationInMillis),
                    noiseDistractionCount = session.noiseEventCount,
                    movementDistractionCount = session.movementEventCount
                )
            }
        } else {
            startTracking()
            updateState { copy(isStarted = true) }
        }
    }

    private fun persistSession(session: Session) = viewModelScope.launch {
        persistSessionUseCase(session).fold(
            onSuccess = {
                logger.d("FocusSessionViewModel", "Persisted!")
            },
            onFailure = {
                logger.e("FocusSessionViewModel", "Failed!!", it)
            }
        )
    }

    private fun stopTracking(): Session {
        val session = sessionManager.stopSession()
        _screenEffects.trySend(FocusSessionScreenEvents.StopService)
        return session!!
    }

    private fun startTracking() {
        sessionManager.startSession()
        _screenEffects.trySend(FocusSessionScreenEvents.StartService)
    }

    fun updateState(transform: FocusSessionScreenState.Success.() -> FocusSessionScreenState) {
        _screenState.update {
            val current =
                (it as? FocusSessionScreenState.Success) ?: FocusSessionScreenState.Success()
            current.transform()
        }
    }

    fun onActivityCreated() {
        viewModelScope.launch {
            sessionManager.currentSession.collect { session ->
                updateState {
                    if (sessionManager.isOngoing() && session != null) {
                        copy(
                            isStarted = true,
                            formattedTime = TimeFormatter.format(session.durationInMillis),
                            noiseDistractionCount = session.noiseEventCount,
                            movementDistractionCount = session.movementEventCount
                        )
                    } else {
                        this
                    }
                }
            }
        }
    }

}