package com.lucasmveigabr.focuswatcher.data.network.dto

import com.lucasmveigabr.focuswatcher.data.database.entity.SessionEntity

data class SessionDto(
    val uuid: String,
    val recordTimestamp: Long = 0L,
    val noiseEventCount: Int = 0,
    val movementEventCount: Int = 0,
    val durationInMillis: Long = 0,
) {
    fun toEntity() =
        SessionEntity(
            id = uuid,
            recordTimestamp = recordTimestamp,
            durationInMillis = durationInMillis,
            noiseEventCount = noiseEventCount,
            movementEventCount = movementEventCount
        )
}

fun SessionEntity.toDto() =
    SessionDto(
        uuid = id,
        recordTimestamp = recordTimestamp,
        noiseEventCount = noiseEventCount,
        movementEventCount = movementEventCount,
        durationInMillis = durationInMillis
    )