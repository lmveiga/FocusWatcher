package com.lucasmveigabr.focuswatcher.core.util

import android.annotation.SuppressLint

object TimeFormatter {

    @SuppressLint("DefaultLocale") // acceptable for MVP
    fun format(durationMillis: Long): String {
        val totalSeconds = durationMillis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

}