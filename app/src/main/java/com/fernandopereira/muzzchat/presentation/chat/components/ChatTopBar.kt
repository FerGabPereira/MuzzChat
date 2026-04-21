package com.fernandopereira.muzzchat.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.fernandopereira.muzzchat.R
import com.fernandopereira.muzzchat.domain.model.User.SARAH
import com.fernandopereira.muzzchat.presentation.chat.extensions.displayName
import com.fernandopereira.muzzchat.ui.common.ThemePreviews
import com.fernandopereira.muzzchat.ui.theme.Dimen8
import com.fernandopereira.muzzchat.ui.theme.ElevationAppBar
import com.fernandopereira.muzzchat.ui.theme.MuzzChatTheme
import com.fernandopereira.muzzchat.ui.theme.SizeAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(onClearChat: () -> Unit = {}) {
    var menuExpanded by remember { mutableStateOf(false) }

    // In dark mode M3 elevation shadows are invisible, so tonalElevation adds a white overlay
    // that visually separates the bar from the body. In light mode the shadow is sufficient
    // and we keep the original surface color to avoid any unintended tint.
    val containerColor = if (isSystemInDarkTheme()) Color.Transparent else MaterialTheme.colorScheme.surface

    Surface(
        shadowElevation = ElevationAppBar,
        tonalElevation = ElevationAppBar,
    ) {
        CenterAlignedTopAppBar(
            title = { ChatAppBarTitle() },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = containerColor,
            ),
            actions = {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.chat_more_options_content_description),
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.chat_clear_chat)) },
                        onClick = {
                            menuExpanded = false
                            onClearChat()
                        },
                    )
                }
            },
        )
    }
}

@Composable
private fun ChatAppBarTitle() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimen8),
    ) {
        Box(
            modifier = Modifier
                .size(SizeAvatar)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = SARAH.displayName().first().toString(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Text(
            text = SARAH.displayName(),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@ThemePreviews
@Composable
private fun ChatTopBarPreview() {
    MuzzChatTheme {
        ChatTopBar()
    }
}
