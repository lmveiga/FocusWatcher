package com.lucasmveigabr.focuswatcher.ui.session

sealed class FocusSessionScreenState {
    data class Success(
        val formattedTime: String = "00:00:00",
        val noiseDistractionCount: Int = 0,
        val movementDistractionCount: Int = 0,
        val isStarted: Boolean = false,
        val previousRecordings: List<SessionUiModel> = emptyList()
    ) : FocusSessionScreenState()
}
