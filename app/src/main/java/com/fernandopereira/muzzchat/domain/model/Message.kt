package com.fernandopereira.muzzchat.domain.model

data class Message(
    val id: Long = 0,
    val text: String,
    val sender: User,
    // epoch millis
    val timestamp: Long,
)
