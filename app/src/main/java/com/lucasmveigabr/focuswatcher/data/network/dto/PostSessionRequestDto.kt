package com.lucasmveigabr.focuswatcher.data.network.dto

data class PostSessionRequestDto(
    val userId: String,
    val sessions: List<SessionDto>
)
