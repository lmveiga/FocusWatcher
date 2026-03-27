package com.lucasmveigabr.focuswatcher.core.util

import java.text.DateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    fun formatToDateTime(millis: Long): String {
        val date = Date(millis)

        val formatter = DateFormat.getDateTimeInstance(
            DateFormat.MEDIUM,
            DateFormat.SHORT,
            Locale.getDefault()
        )

        return formatter.format(date)
    }
}