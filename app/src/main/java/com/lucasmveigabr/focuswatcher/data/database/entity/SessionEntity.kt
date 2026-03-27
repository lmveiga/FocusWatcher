package com.lucasmveigabr.focuswatcher.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lucasmveigabr.focuswatcher.domain.model.Session

@Entity(tableName = "session")
data class SessionEntity(
    @PrimaryKey val id: String,
    val recordTimestamp: Long = 0L,
    val noiseEventCount: Int = 0,
    val movementEventCount: Int = 0,
    val durationInMillis: Long = 0,
    val isSynced: Boolean = false
) {
    fun toDomain() =
        Session(
            recordTimestamp = recordTimestamp,
            durationInMillis = durationInMillis,
            noiseEventCount = noiseEventCount,
            movementEventCount = movementEventCount
        )
}