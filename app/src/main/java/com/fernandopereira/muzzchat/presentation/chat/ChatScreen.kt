package com.fernandopereira.muzzchat.presentation.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fernandopereira.muzzchat.domain.model.User
import com.fernandopereira.muzzchat.presentation.chat.components.ChatTopBar
import com.fernandopereira.muzzchat.presentation.chat.components.DateSectionHeader
import com.fernandopereira.muzzchat.presentation.chat.components.MessageBubble
import com.fernandopereira.muzzchat.presentation.chat.components.MessageInput
import com.fernandopereira.muzzchat.presentation.chat.components.ReplyAsRow
import com.fernandopereira.muzzchat.presentation.chat.model.ChatItem
import com.fernandopereira.muzzchat.ui.theme.Dimen12
import com.fernandopereira.muzzchat.ui.theme.Dimen8
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatScreen(viewModel: ChatViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.items.size) {
        if (uiState.items.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout),
        topBar = { ChatTopBar(onClearChat = { viewModel.onAction(ChatUiAction.ClearChat) }) },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .imePadding(),
        ) {
            // The list is reversed here because `reverseLayout` is a rendering concern.
            // Messages stay in chronological order in the use case, and the UI adapts them
            // for display so the scroll position is preserved when the keyboard opens.
            LazyColumn(
                reverseLayout = true,
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = Dimen12, vertical = Dimen8),
            ) {
                items(items = uiState.items.asReversed(), key = { it.key }) { item ->
                    when (item) {
                        is ChatItem.DateHeader -> {
                            DateSectionHeader(
                                day = item.day,
                                time = item.time,
                            )
                        }

                        is ChatItem.MessageItem -> {
                            MessageBubble(
                                message = item.message,
                                isGroupedWithNext = item.isGroupedWithNext,
                            )
                        }
                    }
                }
            }

            ChatFooter(
                currentUser = uiState.currentUser,
                onUserSelected = { user ->
                    if (user != uiState.currentUser) viewModel.onAction(ChatUiAction.OnSwitchUserClicked)
                },
                inputText = uiState.inputText,
                onTextChange = { viewModel.onAction(ChatUiAction.InputChanged(it)) },
                onSend = { viewModel.onAction(ChatUiAction.OnSendMessageClicked) },
            )
        }
    }
}

@Composable
private fun ChatFooter(
    currentUser: User,
    onUserSelected: (User) -> Unit,
    inputText: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Column {
        HorizontalDivider()
        ReplyAsRow(
            currentUser = currentUser,
            onUserSelected = onUserSelected,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimen12, vertical = Dimen8),
        )
        MessageInput(
            text = inputText,
            onTextChange = onTextChange,
            onSend = onSend,
            modifier =
                Modifier
                    .padding(horizontal = Dimen12)
                    .padding(bottom = Dimen8),
        )
    }
}
