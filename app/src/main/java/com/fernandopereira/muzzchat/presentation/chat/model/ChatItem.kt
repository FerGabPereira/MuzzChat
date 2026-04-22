package com.fernandopereira.muzzchat.presentation.chat.model

import com.fernandopereira.muzzchat.domain.model.Message

sealed interface ChatItem {
    val key: Any

    data class MessageItem(
        val message: Message,
        val isGroupedWithNext: Boolean,
    ) : ChatItem {
        override val key: Any get() = "msg_${message.id}"
    }

    data class DateHeader(
        val day: String,
        val time: String,
        val timestamp: Long,
    ) : ChatItem {
        override val key: Any get() = "header_$timestamp"
    }
}
