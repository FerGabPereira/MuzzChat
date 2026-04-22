package com.fernandopereira.muzzchat.presentation.chat

import com.fernandopereira.muzzchat.domain.model.User
import com.fernandopereira.muzzchat.domain.model.User.ME
import com.fernandopereira.muzzchat.presentation.UiState
import com.fernandopereira.muzzchat.presentation.chat.model.ChatItem

data class ChatUiState(
    val items: List<ChatItem> = emptyList(),
    val inputText: String = "",
    val currentUser: User = ME,
) : UiState
