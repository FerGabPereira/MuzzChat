package com.fernandopereira.muzzchat.presentation.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fernandopereira.muzzchat.domain.model.User.SARAH
import com.fernandopereira.muzzchat.presentation.chat.components.DateSectionHeader
import com.fernandopereira.muzzchat.presentation.chat.components.MessageBubble
import com.fernandopereira.muzzchat.presentation.chat.components.MessageInput
import com.fernandopereira.muzzchat.presentation.chat.components.SenderSelector
import com.fernandopereira.muzzchat.presentation.chat.extensions.displayName
import com.fernandopereira.muzzchat.presentation.chat.model.ChatItem
import com.fernandopereira.muzzchat.ui.theme.Space200
import com.fernandopereira.muzzchat.ui.theme.Space300
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.items.size) {
        if (uiState.items.isNotEmpty()) {
            listState.animateScrollToItem(uiState.items.lastIndex)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = SARAH.displayName(),
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = Space300, vertical = Space200),
            ) {
                items(items = uiState.items, key = { it.key }) { item ->
                    when (item) {
                        is ChatItem.DateHeader -> DateSectionHeader(
                            day = item.day,
                            time = item.time,
                        )
                        is ChatItem.MessageItem -> MessageBubble(
                            message = item.message,
                            isGroupedWithNext = item.isGroupedWithNext,
                        )
                    }
                }
            }

            SenderSelector(
                currentUser = uiState.currentUser,
                onUserSelected = { user ->
                    if (user != uiState.currentUser) viewModel.onAction(ChatUiAction.OnSwitchUserClicked)
                },
                modifier = Modifier.padding(horizontal = Space300, vertical = Space200),
            )

            MessageInput(
                text = uiState.inputText,
                onTextChange = { viewModel.onAction(ChatUiAction.InputChanged(it)) },
                onSend = { viewModel.onAction(ChatUiAction.OnSendMessageClicked) },
                modifier = Modifier
                    .padding(horizontal = Space300)
                    .padding(bottom = Space200),
            )
        }
    }
}
