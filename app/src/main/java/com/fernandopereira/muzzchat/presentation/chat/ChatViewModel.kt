package com.fernandopereira.muzzchat.presentation.chat

import androidx.lifecycle.viewModelScope
import com.fernandopereira.muzzchat.domain.model.Message
import com.fernandopereira.muzzchat.domain.repository.MessageRepository
import com.fernandopereira.muzzchat.domain.usecase.BuildChatItemsUseCase
import com.fernandopereira.muzzchat.presentation.BaseViewModel
import com.fernandopereira.muzzchat.presentation.chat.ChatUiAction.InputChanged
import com.fernandopereira.muzzchat.presentation.chat.ChatUiAction.OnSendMessageClicked
import com.fernandopereira.muzzchat.presentation.chat.ChatUiAction.OnSwitchUserClicked
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: MessageRepository,
    private val buildChatItemsUseCase: BuildChatItemsUseCase,
) : BaseViewModel<ChatUiState, ChatUiAction>(ChatUiState()) {
    init {
        observeMessages()
    }

    override fun onAction(action: ChatUiAction) {
        when (action) {
            is InputChanged -> {
                submitState { copy(inputText = action.text) }
            }

            is OnSendMessageClicked -> {
                sendMessage()
            }

            OnSwitchUserClicked -> {
                submitState { copy(currentUser = currentUser.other()) }
            }
        }
    }

    private fun sendMessage() {
        val currentState = uiState.value
        val trimmedText = currentState.inputText.trim()
        if (trimmedText.isBlank()) return

        // Optimistic clean (better for UX)
        submitState { copy(inputText = "") }

        viewModelScope.launch {
            repository.insert(
                Message(
                    text = trimmedText,
                    sender = currentState.currentUser,
                    timestamp = System.currentTimeMillis(),
                ),
            )
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            repository.messages.collectLatest { messages ->
                submitState {
                    copy(items = buildChatItemsUseCase(messages))
                }
            }
        }
    }
}
