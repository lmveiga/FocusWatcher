package com.lucasmveigabr.focuswatcher.domain.model

import com.lucasmveigabr.focuswatcher.data.database.entity.SessionEntity
import com.lucasmveigabr.focuswatcher.data.network.dto.SessionDto
import java.util.UUID

data class Session(
    val uuid: String = UUID.randomUUID().toString(),
    val recordTimestamp: Long,
    val durationInMillis: Long = 0L,
    val noiseEventCount: Int = 0,
    val movementEventCount: Int = 0
)

fun Session.toDto() = SessionDto(
    uuid = uuid,
    recordTimestamp = recordTimestamp,
    noiseEventCount = noiseEventCount,
    movementEventCount = movementEventCount,
    durationInMillis = durationInMillis
)

fun Session.toEntity(uuid: String = UUID.randomUUID().toString()) =
    SessionEntity(
        id = uuid,
        recordTimestamp = recordTimestamp,
        noiseEventCount = noiseEventCount,
        movementEventCount = movementEventCount,
        durationInMillis = durationInMillis,
        isSynced = false
    )