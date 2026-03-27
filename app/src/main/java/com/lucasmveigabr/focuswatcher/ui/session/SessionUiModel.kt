package com.lucasmveigabr.focuswatcher.ui.session

import com.lucasmveigabr.focuswatcher.core.util.DateFormatter
import com.lucasmveigabr.focuswatcher.core.util.TimeFormatter
import com.lucasmveigabr.focuswatcher.domain.model.Session

data class SessionUiModel(
    val date: String,
    val formattedDuration: String,
    val noiseEvents: Int,
    val movementEvents: Int
)

fun Session.toUiModel() =
    SessionUiModel(
        date = DateFormatter.formatToDateTime(recordTimestamp),
        formattedDuration = TimeFormatter.format(this.durationInMillis),
        noiseEvents = noiseEventCount,
        movementEvents = movementEventCount
    )