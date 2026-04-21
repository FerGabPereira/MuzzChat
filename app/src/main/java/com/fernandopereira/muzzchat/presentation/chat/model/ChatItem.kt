package com.fernandopereira.muzzchat.presentation.chat.model

import com.fernandopereira.muzzchat.domain.model.Message

sealed interface ChatItem {
    data class MessageItem(
        val message: Message,
        val isGroupedWithNext: Boolean,
    ) : ChatItem

    data class DateHeader(val label: String) : ChatItem
}
