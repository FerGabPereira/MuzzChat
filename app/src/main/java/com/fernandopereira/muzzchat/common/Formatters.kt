package com.fernandopereira.muzzchat.common

import java.time.Instant.ofEpochMilli
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale.ENGLISH

private val defaultZoneId = ZoneId.systemDefault()

// For simplicity, we assume the app is always used in the device's local timezone.
private val dayFormatter = DateTimeFormatter.ofPattern("EEEE", ENGLISH)
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", ENGLISH)

fun formatDay(timestamp: Long): String =
    dayFormatter.format(ofEpochMilli(timestamp).atZone(defaultZoneId))

fun formatTime(timestamp: Long): String =
    timeFormatter.format(ofEpochMilli(timestamp).atZone(defaultZoneId))
