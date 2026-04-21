package com.fernandopereira.muzzchat.common

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

internal val dateHeaderFormatter: DateTimeFormatter =
    DateTimeFormatter
        .ofPattern("EEEE HH:mm", Locale.ENGLISH)
        .withZone(ZoneId.systemDefault())
