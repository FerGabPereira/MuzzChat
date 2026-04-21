package com.fernandopereira.muzzchat.presentation.chat

import com.fernandopereira.muzzchat.presentation.UiAction

sealed interface ChatUiAction : UiAction {
    data class InputChanged(val text: String) : ChatUiAction

    data object OnSendMessageClicked : ChatUiAction

    data object OnSwitchUserClicked : ChatUiAction

    data object ClearChat : ChatUiAction
}
