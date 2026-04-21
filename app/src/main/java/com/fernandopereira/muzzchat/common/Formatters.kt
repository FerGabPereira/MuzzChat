package com.fernandopereira.muzzchat.common

import java.time.Instant.ofEpochMilli
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale.ENGLISH

private val defaultZoneId = ZoneId.systemDefault()

// Just for simplification we are gonna assume the app is only available in English
private val dayHeaderFormatter: DateTimeFormatter =
    DateTimeFormatter
        .ofPattern("EEEE", ENGLISH)
        .withZone(defaultZoneId)

private val timeHeaderFormatter: DateTimeFormatter =
    DateTimeFormatter
        .ofPattern("HH:mm", ENGLISH)
        .withZone(defaultZoneId)

internal fun formatDay(timestamp: Long): String =
    dayHeaderFormatter.format(ofEpochMilli(timestamp))

internal fun formatTime(timestamp: Long): String =
    timeHeaderFormatter.format(ofEpochMilli(timestamp))
